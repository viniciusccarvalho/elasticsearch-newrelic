package org.elasticsearch.river.newrelic;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsRequest;
import org.elasticsearch.action.admin.cluster.node.stats.NodesStatsResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.river.AbstractRiverComponent;
import org.elasticsearch.river.River;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;
import org.elasticsearch.threadpool.ThreadPool;

import com.newrelic.api.agent.NewRelic;

public class NewRelicRiver extends AbstractRiverComponent implements River{
	
	private Client client;
	
    @Inject
    public NewRelicRiver(RiverName riverName, RiverSettings settings, Client client, ThreadPool threadPool) {
    	super(riverName, settings);
    	this.client = client;
    	
    	threadPool.schedule(TimeValue.timeValueSeconds(10L), ThreadPool.Names.CACHE, new Runnable() {
			
			public void run() {
				sendData();
			}
		});
    	
     
	}

	public void start() {
		
	}

	public void close() {
		
	}
	
	private void sendData(){
		NodesStatsResponse nodesStats = client.admin().cluster().nodesStats(new NodesStatsRequest()).actionGet();
		Map<String,Float> consolidatedStats = new HashMap<String, Float>();
		
		for(NodeStats stats : nodesStats.getNodes()){
			consolidatedStats.put("indices.size", new Float(stats.getIndices().getStore().getSize().bytesAsInt()));
			consolidatedStats.put("indices.search.average", (float) (stats.getIndices().getSearch().total().getQueryTimeInMillis()/stats.getIndices().getSearch().total().getQueryCount()));
		}
		
		for(String key : consolidatedStats.keySet()){
			NewRelic.recordMetric(key, consolidatedStats.get(key));
		}
		
	}

}
