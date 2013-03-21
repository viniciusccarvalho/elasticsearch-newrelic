package org.elasticsearch.plugin.newrelic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.indices.NodeIndicesStats;
import org.elasticsearch.plugin.newrelic.agents.IndicesAgent;
import org.junit.Test;

public class IndicesAgentTest {

	@Test
	public void collect() throws Exception{
		MapCollector collector = new MapCollector();
		NodeStats nodeStats = mock(NodeStats.class);
		NodeIndicesStats indicesStats = mock(NodeIndicesStats.class);
		
		
		when(nodeStats.getIndices()).thenReturn(indicesStats);
		
		IndicesAgent agent = new IndicesAgent(nodeStats);
		agent.run();
		
	}
	
	
	
}
