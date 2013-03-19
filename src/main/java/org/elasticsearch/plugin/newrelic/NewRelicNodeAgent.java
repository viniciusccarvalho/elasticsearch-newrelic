package org.elasticsearch.plugin.newrelic;

import java.io.IOException;
import java.util.Properties;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;
import org.elasticsearch.plugin.newrelic.agents.HttpAgent;
import org.elasticsearch.plugin.newrelic.agents.IndicesAgent;
import org.elasticsearch.plugin.newrelic.agents.ThreadPoolAgent;
import org.elasticsearch.threadpool.ThreadPool;

public class NewRelicNodeAgent {

	private final Client client;
	private final ThreadPool threadPool;

	private final ESLogger logger = ESLoggerFactory.getLogger(NewRelicNodeAgent.class.getName());
	private final String nodeName;
	private final String clusterName;
	private final Properties properties;

	@Inject
	public NewRelicNodeAgent(Client client, final ThreadPool threadPool, Node node) {
		this.client = client;
		this.threadPool = threadPool;
		this.nodeName = node.settings().get("name");
		this.clusterName = node.settings().get("cluster.name");
		this.properties = new Properties();

		try {
			logger.debug("Loading metrics properties");
			this.properties.load(NewRelicNodeAgent.class.getClassLoader().getResourceAsStream("metrics.properties"));
			if (logger.isDebugEnabled()) {
				for (Object key : properties.keySet()) {
					logger.debug("{}", key);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not read metrics file, plugin can not be loaded");
		}
		threadPool.scheduleWithFixedDelay(new Runnable() {

			public void run() {
				sendData(threadPool);
			}
		}, TimeValue.timeValueSeconds(10L));

	}

	private void sendData(ThreadPool threadPool) {
		// TODO: There should be an way to get the node Id, but at construction
		// we don't have it
		NodesStatsResponse response = client.admin().cluster().nodesStats(new NodesStatsRequest()).actionGet();
		NodeStats node = null;
		for (NodeStats n : response.nodes()) {
			if (n.node().getName().equals(this.nodeName)) {
				node = n;
				break;
			}
		}
		if (node != null) {
			threadPool.generic().execute(new HttpAgent(node));
			threadPool.generic().execute(new IndicesAgent(node));
			threadPool.generic().execute(new ThreadPoolAgent(node));
		}
	}

}