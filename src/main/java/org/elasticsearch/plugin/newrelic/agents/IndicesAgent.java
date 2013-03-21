package org.elasticsearch.plugin.newrelic.agents;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.index.search.stats.SearchStats;
import org.elasticsearch.indices.NodeIndicesStats;


public class IndicesAgent extends NodeAgent implements Runnable{

	public IndicesAgent(NodeStats nodeStats){
		super(nodeStats);
	}
	
	public void run() {

		NodeIndicesStats indiceStats = nodeStats.getIndices();
		SearchStats searchStats = indiceStats.getSearch();
		
		float qpms = (float)searchStats.total().getQueryCount()/Math.max(1, searchStats.total().getQueryTimeInMillis());
		float fpms = (float)searchStats.total().getFetchCount()/Math.max(1, searchStats.total().getFetchTimeInMillis());
		float gpms = (float)indiceStats.get().getCount()/Math.max(1, indiceStats.get().getTimeInMillis());
		
		collector.recordMetric("indices.search.query_per_second", qpms*1000 );
		collector.recordMetric("indices.search,query_time_millis", 1/qpms);
		collector.recordMetric("indices.search.fetch_per_second",fpms*1000);
		collector.recordResponseTimeMetric("indices.search.fetch_time_millis",(long) (1/fpms));
		collector.recordMetric("indices.get.get_per_second",gpms*1000);
		collector.recordResponseTimeMetric("indices.get.get_time_millis",(long) (1/gpms));
		collector.recordMetric("indices.get.exists",indiceStats.get().existsCount());
		collector.recordMetric("indices.get.missing",indiceStats.get().missingCount());
		collector.recordMetric("indices.cache.field_evictions",indiceStats.cache().getFieldEvictions());
		collector.recordMetric("indices.cache.filter_evictions",indiceStats.cache().getFilterEvictions());
		collector.recordMetric("indices.cache.filter_count",indiceStats.cache().getFilterCount());
		collector.recordMetric("indices.cache.field_size",indiceStats.cache().getFieldSizeInBytes());
		collector.recordMetric("indices.cache.filter_size",indiceStats.cache().getFilterSizeInBytes());
		collector.recordMetric("indices.doc.count", indiceStats.docs().count());
		collector.recordMetric("indices.doc.deleted", indiceStats.docs().deleted());
		collector.recordMetric("indices.store.size", indiceStats.store().sizeInBytes());
		collector.recordMetric("indices.indexing.index_total", indiceStats.indexing().total().indexCount());
		collector.recordResponseTimeMetric("indices.indexing.index_time_millis", indiceStats.indexing().total().indexTimeInMillis());
		collector.recordMetric("indices.indexing.delete_total", indiceStats.indexing().total().deleteCount());
		collector.recordResponseTimeMetric("indices.indexing.delete_time_millis", indiceStats.indexing().total().deleteTimeInMillis());
		
	}

}
