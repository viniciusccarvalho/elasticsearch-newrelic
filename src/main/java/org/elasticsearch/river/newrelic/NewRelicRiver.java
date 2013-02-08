package org.elasticsearch.river.newrelic;

import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.river.AbstractRiverComponent;
import org.elasticsearch.river.River;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;
import org.elasticsearch.threadpool.ThreadPool;

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
		client.admin().cluster().state(new ClusterStateRequest()).actionGet().getState();
	}

}
