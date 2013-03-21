package org.elasticsearch.plugin.newrelic.agents;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.plugin.newrelic.collector.MetricCollector;

public abstract class NodeAgent {
	
	protected NodeStats nodeStats;
	
	protected MetricCollector collector;
	
	public NodeAgent(NodeStats nodeStats) {
		this.nodeStats = nodeStats;
	}

	public void setCollector(MetricCollector collector) {
		this.collector = collector;
	}
	
}
