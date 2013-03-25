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
import org.elasticsearch.transport.TransportStats;

public class TransportAgent extends NodeAgent {

	@Override
	public void execute(NodeStats nodeStats) {
		TransportStats transportStats = nodeStats.getTransport();
		
		if(transportStats != null){
			logger.debug("Running TransportAgent");
			collector.recordMetric("transport/rx/count", transportStats.rxCount());
			collector.recordMetric("transport/rx/size", transportStats.rxSize().bytes());
			collector.recordMetric("transport/rx/averageSize", (float)transportStats.rxSize().bytes()/Math.max(1.0, transportStats.rxCount()));
			collector.recordMetric("transport/tx/count", transportStats.getTxCount());
			collector.recordMetric("transport/tx/size", transportStats.txSize().bytes());
			collector.recordMetric("transport/tx/averageSize", (float)transportStats.txSize().bytes()/Math.max(1.0, transportStats.txCount()));
			collector.recordMetric("transport/open", transportStats.serverOpen());
		}
	}

	@Override
	public String getName() {
		return "transport";
	}

}
