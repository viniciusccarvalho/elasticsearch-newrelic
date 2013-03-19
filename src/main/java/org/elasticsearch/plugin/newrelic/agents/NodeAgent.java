package org.elasticsearch.plugin.newrelic.agents;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;

public abstract class NodeAgent {
	protected NodeStats nodeStats;

	public NodeAgent(NodeStats nodeStats) {
		this.nodeStats = nodeStats;
	}
	
}
