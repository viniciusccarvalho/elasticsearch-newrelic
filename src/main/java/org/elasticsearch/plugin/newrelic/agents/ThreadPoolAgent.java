package org.elasticsearch.plugin.newrelic.agents;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.threadpool.ThreadPoolStats;
import org.elasticsearch.threadpool.ThreadPoolStats.Stats;

public class ThreadPoolAgent extends NodeAgent implements Runnable {

	public ThreadPoolAgent(NodeStats nodeStats) {
		super(nodeStats);
	}

	@Override
	public void run() {
		ThreadPoolStats poolStats = nodeStats.threadPool();
		while(poolStats.iterator().hasNext()){
			Stats stats = poolStats.iterator().next();
			collector.recordMetric("pool."+stats.getName()+".active", stats.active());
			collector.recordMetric("pool."+stats.getName()+".queue", stats.queue());
			collector.recordMetric("pool."+stats.getName()+".rejected", stats.rejected());
			collector.recordMetric("pool."+stats.getName()+".threads", stats.threads());
		}
	}

}
