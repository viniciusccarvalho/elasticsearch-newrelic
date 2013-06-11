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

import java.util.Iterator;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.threadpool.ThreadPoolStats;
import org.elasticsearch.threadpool.ThreadPoolStats.Stats;

public class ThreadPoolAgent extends NodeAgent {

	@Override
	public void execute(NodeStats nodeStats) {
		ThreadPoolStats poolStats = nodeStats.getThreadPool();
		if(poolStats != null){
			logger.debug("Running ThreadPoolAgent");
			Iterator<Stats> it = poolStats.iterator();
			while(it.hasNext()){
				Stats stats = it.next();
				if(stats != null){
					collector.recordMetric("pool/"+stats.getName()+"/active", stats.getActive());
					collector.recordMetric("pool/"+stats.getName()+"/queue", stats.getQueue());
					collector.recordMetric("pool/"+stats.getName()+"/rejected", stats.getRejected());
					collector.recordMetric("pool/"+stats.getName()+"/threads", stats.getThreads());
				}
			}
		}		
	}

	@Override
	public String getName() {
		return "pool";
	}

}
