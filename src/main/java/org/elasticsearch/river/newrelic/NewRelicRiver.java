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
import org.elasticsearch.river.AbstractRiverComponent;
import org.elasticsearch.river.River;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;
import org.elasticsearch.threadpool.ThreadPool;

import com.newrelic.api.agent.NewRelic;

public class NewRelicRiver extends AbstractRiverComponent implements River {

	private Client client;

	private final ThreadPool threadPool;

	@Inject
	public NewRelicRiver(RiverName riverName, RiverSettings settings, Client client, ThreadPool threadPool) {
		super(riverName, settings);
		this.client = client;
		logger.debug("Configuring NewRelic River");
		this.threadPool = threadPool;

	}

	public void start() {
		logger.debug("Starting NewRelic River");
		threadPool.scheduleWithFixedDelay(new Runnable() {

			public void run() {
				sendData();
			}
		},TimeValue.timeValueSeconds(10L));
	}

	public void close() {

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
