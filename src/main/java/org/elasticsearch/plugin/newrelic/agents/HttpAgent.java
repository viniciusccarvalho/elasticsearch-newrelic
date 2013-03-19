package org.elasticsearch.plugin.newrelic.agents;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.http.HttpStats;

import com.newrelic.api.agent.NewRelic;

public class HttpAgent extends NodeAgent implements Runnable{

	
	public HttpAgent(NodeStats nodeStats) {
		super(nodeStats);
	}

	@Override
	public void run() {
		HttpStats httpStats = nodeStats.getHttp();
		NewRelic.recordMetric("http.current_open", httpStats.getServerOpen());
		NewRelic.recordMetric("http.total_open", httpStats.getTotalOpen());
	}

}
