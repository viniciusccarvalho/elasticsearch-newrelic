package org.elasticsearch.river.newrelic;

import java.util.HashMap;
import java.util.Map;

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
	
	private final ESLogger logger = ESLoggerFactory.getLogger("NewRelicNodeAgent");
	private final String nodeName;
	private final String clusterName;
	
	@Inject
	public NewRelicNodeAgent(Client client, ThreadPool threadPool, Node node){
		this.client = client;
		this.threadPool = threadPool;
		this.nodeName = node.settings().get("name");
		this.clusterName = node.settings().get("cluster.name");
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
			
			
			logger.debug("Recording data to new relic");
			
			for (String key : consolidatedStats.keySet()) {
				NewRelic.recordMetric(key, consolidatedStats.get(key));
				logger.debug("[{}] : {} ", key, consolidatedStats.get(key));
			}
			
		}
		
		

	}
	
	private void addIndicesStats(Map<String,Float> map, NodeStats stats){
		String prefix = this.clusterName+"."+this.nodeName+".indices";
		map.put(prefix+".store.size", new Float((int)stats.getIndices().getStore().getSizeInBytes()));
		map.put(prefix+".cache.fieldEvictions", new Float(stats.getIndices().getCache().fieldEvictions()));
		
	}
	
}
