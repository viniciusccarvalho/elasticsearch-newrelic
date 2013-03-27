/*
 * Licensed to ElasticSearch under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.plugin.newrelic.agents;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.plugin.newrelic.model.Metric;
import org.elasticsearch.transport.TransportStats;

public class TransportAgent extends NodeAgent {
	
	public TransportAgent() {
		metrics.put("transport/rx/count",new Metric(0.0,true,"transport/rx/count"));
		metrics.put("transport/rx/size",new Metric(0.0,true,"transport/rx/size"));
		metrics.put("transport/tx/count",new Metric(0.0,true,"transport/tx/count"));
		metrics.put("transport/tx/size",new Metric(0.0,true,"transport/tx/size"));
		metrics.put("transport/open",new Metric("transport/tx/size"));
		
	}
	
	@Override
	public void execute(NodeStats nodeStats) {
		TransportStats transportStats = nodeStats.getTransport();
		
		if(transportStats != null){
			logger.debug("Running TransportAgent");
			collector.recordMetric(metrics.get("transport/rx/count").refresh( transportStats.rxCount()));
			collector.recordMetric(metrics.get("transport/rx/size").refresh( transportStats.rxSize().bytes()));
			collector.recordMetric(metrics.get("transport/tx/count").refresh( transportStats.getTxCount()));
			collector.recordMetric(metrics.get("transport/tx/size").refresh( transportStats.txSize().bytes()));
			collector.recordMetric(metrics.get("transport/open").refresh( transportStats.serverOpen()));
		}
	}

	@Override
	public String getName() {
		return "transport";
	}

}
