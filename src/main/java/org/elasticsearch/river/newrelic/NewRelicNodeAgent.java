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
	
	private final ESLogger logger = ESLoggerFactory.getLogger(NewRelicNodeAgent.class.getName());
	
	
	@Inject
	public NewRelicNodeAgent(Client client, ThreadPool threadPool, Node node){
		this.client = client;
		this.threadPool = threadPool;
		
		threadPool.scheduleWithFixedDelay(new Runnable() {

			public void run() {
				sendData();
			}
		},TimeValue.timeValueSeconds(10L));
		
	}
	
	
	private void sendData() {
		long start = 0L;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
			logger.debug("Fetching data from nodes");
		}

		NodesStatsResponse nodesStats = client.admin().cluster().nodesStats(new NodesStatsRequest()).actionGet();
		if (logger.isDebugEnabled()) {
			logger.debug("Data fetched in {} ms", System.currentTimeMillis() - start);
		}
		Map<String, Float> consolidatedStats = new HashMap<String, Float>();

		
		for (NodeStats stats : nodesStats.getNodes()) {
			
			if (stats.getIndices() != null) {
				consolidatedStats.put("indices.size", new Float(stats.getIndices().getStore().getSize().bytesAsInt()));
				if (stats.getIndices().getSearch().total().getQueryCount() != 0) {
					consolidatedStats.put("indices.search.average", (float) (stats.getIndices().getSearch().total().getQueryTimeInMillis() / stats.getIndices().getSearch().total().getQueryCount()));
				}
			}
		}
		logger.debug("Recording data to new relic");
		for (String key : consolidatedStats.keySet()) {
			NewRelic.recordMetric(key, consolidatedStats.get(key));
			logger.debug("[{}] : {} ", key, consolidatedStats.get(key));
		}

	}
	
}
