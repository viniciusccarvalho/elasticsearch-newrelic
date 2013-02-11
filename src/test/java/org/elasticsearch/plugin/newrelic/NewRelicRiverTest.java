package org.elasticsearch.plugin.newrelic;

import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;
import org.elasticsearch.river.newrelic.NewRelicRiver;
import org.elasticsearch.threadpool.ThreadPool;
import org.junit.Test;

public class NewRelicRiverTest {

	@Test
	public void sendData() throws Exception{
		Node node = NodeBuilder.nodeBuilder().build();
		NewRelicRiver river = new NewRelicRiver(new RiverName("newrelic", "newrelish"), new RiverSettings(node.settings(), null), node.client(), new ThreadPool());
		river.start();
		while(true){
			Thread.sleep(15000L);
		}
	}
	
	
}
