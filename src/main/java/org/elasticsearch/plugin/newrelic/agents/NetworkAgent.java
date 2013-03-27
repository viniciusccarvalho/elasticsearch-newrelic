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
import org.elasticsearch.plugin.newrelic.model.Metric;

public class NetworkAgent extends NodeAgent {

	public NetworkAgent() {
		metrics.put("network/tcp/activeOpen", new Metric("network/tcp/activeOpen"));
		metrics.put("network/tcp/passiveOpen", new Metric("network/tcp/passiveOpen"));
		metrics.put("network/tcp/currentEstabilished", new Metric("network/tcp/currentEstabilished"));
		metrics.put("network/tcp/inSegs", new Metric("network/tcp/inSegs"));
		metrics.put("network/tcp/outSegs", new Metric("network/tcp/outSegs"));
		metrics.put("network/tcp/retranSegs", new Metric("network/tcp/retranSegs"));
		metrics.put("network/tcp/estabResets", new Metric("network/tcp/estabResets"));
		metrics.put("network/tcp/attemptsFails", new Metric("network/tcp/attemptsFails"));
		metrics.put("network/tcp/inErrs", new Metric("network/tcp/inErrs"));
		metrics.put("network/tcp/outRsts", new Metric("network/tcp/outRsts"));

	}

	@Override
	public void execute(NodeStats nodeStats) {
		NetworkStats networkStats =  nodeStats.network();
		
		if(networkStats != null){
			logger.debug("Running NetworkAgent");
			collector.recordMetric(metrics.get("network/tcp/activeOpen").refresh(networkStats.tcp().activeOpens()));
			collector.recordMetric(metrics.get("network/tcp/passiveOpen").refresh(networkStats.tcp().passiveOpens()));
			collector.recordMetric(metrics.get("network/tcp/currentEstabilished").refresh(networkStats.tcp().currEstab()));

			collector.recordMetric(metrics.get("network/tcp/inSegs").refresh( networkStats.tcp().inSegs()));
			collector.recordMetric(metrics.get("network/tcp/outSegs").refresh( networkStats.tcp().outSegs()));
			collector.recordMetric(metrics.get("network/tcp/retranSegs").refresh( networkStats.tcp().retransSegs()));
			collector.recordMetric(metrics.get("network/tcp/estabResets").refresh( networkStats.tcp().estabResets()));
			collector.recordMetric(metrics.get("network/tcp/attemptsFails").refresh( networkStats.tcp().attemptFails()));
			collector.recordMetric(metrics.get("network/tcp/inErrs").refresh( networkStats.tcp().inErrs()));
			collector.recordMetric(metrics.get("network/tcp/outRsts").refresh( networkStats.tcp().outRsts()));
			
		}

	}

	@Override
	public String getName() {
		return "network";
	}

}
