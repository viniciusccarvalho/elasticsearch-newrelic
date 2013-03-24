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
package org.elasticsearch.plugin.newrelic;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;
import org.elasticsearch.plugin.newrelic.agents.HttpAgent;
import org.elasticsearch.plugin.newrelic.agents.IndicesAgent;
import org.elasticsearch.plugin.newrelic.agents.NodeAgent;
import org.elasticsearch.plugin.newrelic.agents.ThreadPoolAgent;
import org.elasticsearch.threadpool.ThreadPool;

public class NewRelicNodeAgent {

	private final Client client;

	private final ESLogger logger = ESLoggerFactory.getLogger(NewRelicNodeAgent.class.getName());
	private final String nodeName;
	private final Map<String, NodeAgent> agents;

	@Inject
	public NewRelicNodeAgent(Client client, final ThreadPool threadPool, Node node) {
		this.client = client;
		this.nodeName = node.settings().get("name");
		this.agents = new HashMap<String, NodeAgent>();
		setupAgents();
		threadPool.scheduleWithFixedDelay(new Runnable() {

			public void run() {
				sendData();
			}
		}, TimeValue.timeValueSeconds((Long) Configuration.getInstance().get("refreshInterval")));

	}
	
	
	private void setupAgents() {
		Configuration.getInstance().put("http", true);
		Configuration.getInstance().put("indices", true);
		Configuration.getInstance().put("pool", true);
		Configuration.getInstance().put("refreshInterval", 10L);
		this.agents.put("http", new HttpAgent());
		this.agents.put("indices",new IndicesAgent());
		this.agents.put("pool", new ThreadPoolAgent());
		
	}

	private void sendData() {
		// TODO: There should be an way to get the node Id, but at construction
		// we don't have it
		NodesStatsResponse response = client.admin().cluster().nodesStats(new NodesStatsRequest().all()).actionGet();
		NodeStats node = null;
		for (NodeStats n : response.nodes()) {
			if (n.node().getName().equals(this.nodeName)) {
				node = n;
				break;
			}
		}
		if (node != null) {
			for(String agent : agents.keySet()){
				if((Boolean) Configuration.getInstance().get(agent)){
					agents.get(agent).execute(node);
				}
					
			}
		}
	}

}
