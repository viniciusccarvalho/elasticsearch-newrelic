package org.elasticsearch.plugin.newrelic.agents;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.index.search.stats.SearchStats;
import org.elasticsearch.indices.NodeIndicesStats;

import com.newrelic.api.agent.NewRelic;

public class IndicesAgent extends NodeAgent implements Runnable{

	public IndicesAgent(NodeStats nodeStats){
		super(nodeStats);
	}
	
	public void run() {
		Map<String, Float> stats = new HashMap<String, Float>();
		NodeIndicesStats indiceStats = nodeStats.getIndices();
		SearchStats searchStats = indiceStats.getSearch();
		float qpms = (float)searchStats.total().getQueryCount()/searchStats.total().getQueryTimeInMillis();
		float fpms = (float)searchStats.total().getFetchCount()/searchStats.total().getFetchTimeInMillis();
		float gpms = (float)indiceStats.get().getCount()/indiceStats.get().getTimeInMillis();
		NewRelic.recordMetric("indices.search.query_per_second", qpms*1000 );
		NewRelic.recordMetric("indices.search,query_time_millis", 1/qpms);
		NewRelic.recordMetric("indices.search.fetch_per_second",fpms*1000);
		NewRelic.recordResponseTimeMetric("indices.search.fetch_time_millis",(long) (1/fpms));
		NewRelic.recordMetric("indices.get.get_per_second",gpms*1000);
		NewRelic.recordResponseTimeMetric("indices.get.get_time_millis",(long) (1/gpms));
		NewRelic.recordMetric("indices.get.exists",indiceStats.get().existsCount());
		NewRelic.recordMetric("indices.get.missing",indiceStats.get().missingCount());
		NewRelic.recordMetric("indices.cache.field_evictions",(float)indiceStats.cache().getFieldEvictions());
		NewRelic.recordMetric("indices.cache.filter_evictions",(float)indiceStats.cache().getFilterEvictions());
		NewRelic.recordMetric("indices.cache.filter_count",(float)indiceStats.cache().getFilterCount());
		NewRelic.recordMetric("indices.cache.field_size",(float)indiceStats.cache().getFieldSizeInBytes());
		NewRelic.recordMetric("indices.cache.filter_size",(float)indiceStats.cache().getFilterSizeInBytes());
		NewRelic.recordMetric("indices.doc.count", (float)indiceStats.docs().count());
		NewRelic.recordMetric("indices.doc.deleted", (float)indiceStats.docs().deleted());
		NewRelic.recordMetric("indices.store.size", (float)indiceStats.store().sizeInBytes());
		NewRelic.recordMetric("indices.indexing.index_total", (float)indiceStats.indexing().total().indexCount());
		NewRelic.recordResponseTimeMetric("indices.indexing.index_time_millis", indiceStats.indexing().total().indexTimeInMillis());
		NewRelic.recordMetric("indices.indexing.delete_total", (float)indiceStats.indexing().total().deleteCount());
		NewRelic.recordResponseTimeMetric("indices.indexing.delete_time_millis", indiceStats.indexing().total().deleteTimeInMillis());
		
	}

}
