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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.index.search.stats.SearchStats;
import org.elasticsearch.index.search.stats.SearchStats.Stats;
import org.elasticsearch.indices.NodeIndicesStats;
import org.elasticsearch.plugin.newrelic.agents.HttpAgent;
import org.elasticsearch.plugin.newrelic.agents.IndicesAgent;
import org.junit.Test;


public class IndicesAgentTest {

	@Test
	public void nullValues() throws Exception{
		MapCollector collector = new MapCollector();
		NodeStats nodeStats = mock(NodeStats.class);
		NodeIndicesStats indicesStats = mock(NodeIndicesStats.class);
		
		
		when(nodeStats.getIndices()).thenReturn(indicesStats);
		
		IndicesAgent agent = new IndicesAgent();
		agent.execute(nodeStats);
		Assert.assertTrue(collector.getStats().isEmpty());
		
	}
	
	
	@Test
	public void zeroDivision() throws Exception{
		MapCollector collector = new MapCollector();
		NodeStats nodeStats = mock(NodeStats.class);
		NodeIndicesStats indicesStats = mock(NodeIndicesStats.class);
		
		SearchStats searchStats = mock(SearchStats.class);
		Stats total = mock(Stats.class);
		
		when(nodeStats.getIndices()).thenReturn(indicesStats);
		when(indicesStats.getSearch()).thenReturn(searchStats);
		when(searchStats.getTotal()).thenReturn(total);
		when(total.getQueryCount()).thenReturn(1L);
		when(total.getQueryTimeInMillis()).thenReturn(0L);
		IndicesAgent agent = new IndicesAgent();
		agent.setCollector(collector);
		agent.execute(nodeStats);
		Assert.assertFalse(collector.getStats().isEmpty());
	}
	
	@Test
	public void queriesPerSecond(){
		MapCollector collector = new MapCollector();
		NodeStats nodeStats = mock(NodeStats.class);
		NodeIndicesStats indicesStats = mock(NodeIndicesStats.class);
		
		SearchStats searchStats = mock(SearchStats.class);
		Stats total = new Stats(3000L, 500L, 100L, 100L, 2L, 3L);
		
		when(nodeStats.getIndices()).thenReturn(indicesStats);
		when(indicesStats.getSearch()).thenReturn(searchStats);
		when(searchStats.getTotal()).thenReturn(total);
		
		IndicesAgent agent = new IndicesAgent();
		agent.setCollector(collector);
		agent.execute(nodeStats);
		Assert.assertEquals(6000.0f, collector.getStats().get("indices/search/per_second"));
		Assert.assertEquals(1/6f, collector.getStats().get("indices/search/average_time_millis"));
	}
	
	
	
}
