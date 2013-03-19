package org.elasticsearch.plugin.newrelic.agents;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.threadpool.ThreadPoolStats;
import org.elasticsearch.threadpool.ThreadPoolStats.Stats;

import com.newrelic.api.agent.NewRelic;

public class ThreadPoolAgent extends NodeAgent implements Runnable {

	public ThreadPoolAgent(NodeStats nodeStats) {
		super(nodeStats);
	}

	@Override
	public void run() {
		ThreadPoolStats poolStats = nodeStats.threadPool();
		while(poolStats.iterator().hasNext()){
			Stats stats = poolStats.iterator().next();
			NewRelic.recordMetric("pool."+stats.getName()+".active", stats.active());
			NewRelic.recordMetric("pool."+stats.getName()+".queue", stats.queue());
			NewRelic.recordMetric("pool."+stats.getName()+".rejected", stats.rejected());
			NewRelic.recordMetric("pool."+stats.getName()+".threads", stats.threads());
		}
	}

}
