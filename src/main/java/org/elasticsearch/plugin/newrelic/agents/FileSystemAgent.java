package org.elasticsearch.plugin.newrelic.agents;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.monitor.fs.FsStats;
import org.elasticsearch.monitor.fs.FsStats.Info;

public class FileSystemAgent extends NodeAgent {
	
	private Long diskReads = Long.MAX_VALUE;
	private Long diskWrites = Long.MAX_VALUE;
	private Long readSize = Long.MAX_VALUE;
	private Long writeSize = Long.MAX_VALUE;
	
	private Lock lock = new ReentrantLock();
	
	@Override
	public void execute(NodeStats nodeStats) {
		FsStats fsStats = nodeStats.getFs();
		Long totalReads = 0L;
		Long totalWrites = 0L;
		Long totalReadSize = 0L;
		Long totalWriteSize = 0L;
		
		if(fsStats != null){
			Iterator<Info> it = fsStats.iterator();
			while(it.hasNext()){
				Info fsInfo = it.next();
				totalReads += fsInfo.getDiskReads();
				totalWrites += fsInfo.getDiskWrites();
				totalReadSize += fsInfo.getDiskReadSizeInBytes();
				totalWriteSize += fsInfo.getDiskWriteSizeInBytes();
			}
			
			try {
				if(lock.tryLock(500, TimeUnit.MILLISECONDS)){
					if(totalReads - diskReads >= 0){
						collector.recordMetric("fs/read/total", totalReads);
					}
					if(totalWrites - diskWrites >= 0){
						collector.recordMetric("fs/write/total", totalWrites);
					}
					if(totalReadSize - readSize >=0){
						collector.recordMetric("fs/read/size", totalReadSize);
					}
					if(totalWriteSize - writeSize >= 0){
						collector.recordMetric("fs/write/size", totalWriteSize);
					}
					totalReads = diskReads;
					totalWrites = diskWrites;
					readSize = totalReadSize;
					writeSize = totalWriteSize;
				}
				
			} catch (Exception e) {
			}finally {
				lock.unlock();
			}
			
			
		}
	}

	@Override
	public String getName() {
		return "fs";
	}

}
