package org.elasticsearch.river.newrelic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;
import org.elasticsearch.threadpool.ThreadPool;

import com.newrelic.api.agent.NewRelic;

public class NewRelicNodeAgent {

	private final Client client;
	private final ThreadPool threadPool;
	
	private final ESLogger logger = ESLoggerFactory.getLogger(NewRelicNodeAgent.class.getName());
	private final String nodeName;
	private final String clusterName;
	private final JexlEngine jexlEngine;
	private final Properties properties;
	
	@Inject
	public NewRelicNodeAgent(Client client, ThreadPool threadPool, Node node){
		this.client = client;
		this.jexlEngine = new JexlEngine();
		this.threadPool = threadPool;
		this.nodeName = node.settings().get("name");
		this.clusterName = node.settings().get("cluster.name");
		this.properties = new Properties();
		
		try {
			logger.debug("Loading metrics properties");
			this.properties.load(NewRelicNodeAgent.class.getClassLoader().getResourceAsStream("metrics.properties"));
			if(logger.isDebugEnabled()){
				for(Object key : properties.keySet()){
					logger.debug("{}",key);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not read metrics file, plugin can not be loaded");
		}
		threadPool.scheduleWithFixedDelay(new Runnable() {

			public void run() {
				sendData();
			}
		},TimeValue.timeValueSeconds(10L));
		
	}
	
	
	private void sendData() {
		//TODO: There should be an way to get the node Id, but at construction we don't have it
		
		NodesStatsResponse response = client.admin().cluster().nodesStats(new NodesStatsRequest()).actionGet();
		NodeStats node = null;
		for(NodeStats n : response.nodes()){
			if(n.node().getName().equals(this.nodeName)){
				node = n;
				break;
			}
		}
		if(node != null){
			
			Map<String, Float> consolidatedStats = new HashMap<String, Float>();
			JexlContext context = new MapContext();
			context.set("node", node);
			for(Object key : properties.keySet()){
				logger.debug("Creating expression for {}", key);
				Expression e = jexlEngine.createExpression("node."+key);
				Object value = e.evaluate(context);
				if(value != null){
					consolidatedStats.put("org.elasticsearch.node."+key, Float.valueOf(String.valueOf(value)));
				}
			}
			
			logger.debug("Recording data to new relic");
			
			for (String key : consolidatedStats.keySet()) {
				NewRelic.recordMetric(key, consolidatedStats.get(key));
				logger.debug("[{}] : {} ", key, consolidatedStats.get(key));
			}
			
		}
	}
	
	private void indicesStats(Map<String,Float> consolidatedStats, NodeStats node) {
	}
	
	private void addIndicesStats(Map<String,Float> map, NodeStats stats){
		String prefix = this.clusterName+"."+this.nodeName+".indices";
		map.put(prefix+".store.size", new Float((int)stats.getIndices().getStore().getSizeInBytes()));
		map.put(prefix+".cache.fieldEvictions", new Float(stats.getIndices().getCache().getFieldEvictions()));
		
	}
	

	
}
