package org.elasticsearch.plugin.newrelic.agents;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.http.HttpStats;

public class HttpAgent extends NodeAgent implements Runnable{

	
	public HttpAgent(NodeStats nodeStats) {
		super(nodeStats);
	}

	@Override
	public void run() {
		HttpStats httpStats = nodeStats.getHttp();
		collector.recordMetric("http.current_open", httpStats.getServerOpen());
		collector.recordMetric("http.total_open", httpStats.getTotalOpen());
	}

}
