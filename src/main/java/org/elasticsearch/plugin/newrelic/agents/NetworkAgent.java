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
import org.elasticsearch.monitor.network.NetworkStats;

public class NetworkAgent extends NodeAgent {

	@Override
	public void execute(NodeStats nodeStats) {
		NetworkStats networkStats =  nodeStats.network();
		
		if(networkStats != null){
			logger.debug("Running NetworkAgent");
			collector.recordMetric("network/tcp/activeOpen", networkStats.tcp().activeOpens());
			collector.recordMetric("network/tcp/passiveOpen", networkStats.tcp().passiveOpens());
			collector.recordMetric("network/tcp/currentEstabilished", networkStats.tcp().currEstab());
			collector.recordMetric("network/tcp/inSegs", networkStats.tcp().inSegs());
			collector.recordMetric("network/tcp/outSegs", networkStats.tcp().outSegs());
			collector.recordMetric("network/tcp/retranSegs", networkStats.tcp().retransSegs());
			collector.recordMetric("network/tcp/estabResets", networkStats.tcp().estabResets());
			collector.recordMetric("network/tcp/attemptsFails", networkStats.tcp().attemptFails());
			collector.recordMetric("network/tcp/inErrs", networkStats.tcp().inErrs());
			collector.recordMetric("network/tcp/outRsts", networkStats.tcp().outRsts());
			
		}

	}

	@Override
	public String getName() {
		return "network";
	}

}
