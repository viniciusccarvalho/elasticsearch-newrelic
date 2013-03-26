package org.elasticsearch.plugin.newrelic.agents;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.monitor.fs.FsStats;
import org.elasticsearch.monitor.fs.FsStats.Info;
import org.elasticsearch.plugin.newrelic.model.Metric;

public class FileSystemAgent extends NodeAgent {
	
	
	public FileSystemAgent(){
		this.metrics.put("fs/read/total", new Metric(0.0,true,"fs/read/total"));
		this.metrics.put("fs/write/total", new Metric(0.0,true,"fs/write/total"));
		this.metrics.put("fs/read/size", new Metric(0.0,true,"fs/read/size"));
		this.metrics.put("fs/write/size", new Metric(0.0,true,"fs/write/size"));
	}
	
	@Override
	public void execute(NodeStats nodeStats) {
		FsStats fsStats = nodeStats.getFs();
		
		if(fsStats != null){
			Iterator<Info> it = fsStats.iterator();
			while(it.hasNext()){
				Info fsInfo = it.next();
				collector.recordMetric(metrics.get("fs/read/total").refresh(fsInfo.diskReads()));
				collector.recordMetric(metrics.get("fs/write/total").refresh(fsInfo.diskWrites()));
				collector.recordMetric(metrics.get("fs/read/size").refresh(fsInfo.diskReadSizeInBytes()));
				collector.recordMetric(metrics.get("fs/write/size").refresh(fsInfo.diskWriteSizeInBytes()));
			}
			
			
			
		}
	}

	@Override
	public String getName() {
		return "fs";
	}

}
