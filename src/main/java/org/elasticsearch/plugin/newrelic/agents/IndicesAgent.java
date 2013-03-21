package org.elasticsearch.plugin.newrelic.agents;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.index.cache.CacheStats;
import org.elasticsearch.index.get.GetStats;
import org.elasticsearch.index.search.stats.SearchStats;
import org.elasticsearch.index.shard.DocsStats;
import org.elasticsearch.indices.NodeIndicesStats;

public class IndicesAgent extends NodeAgent implements Runnable {

	public IndicesAgent(NodeStats nodeStats) {
		super(nodeStats);
	}

	public void run() {

		NodeIndicesStats indiceStats = nodeStats.getIndices();
		if (indiceStats != null) {

			SearchStats searchStats = indiceStats.getSearch();
			GetStats getStats = indiceStats.get();
			CacheStats cacheStats = indiceStats.cache();
			DocsStats docStats = indiceStats.docs();

			if (searchStats != null) {
				float qpms = (float) searchStats.total().getQueryCount() / Math.max(1, searchStats.total().getQueryTimeInMillis());
				float fpms = (float) searchStats.total().getFetchCount() / Math.max(1, searchStats.total().getFetchTimeInMillis());
				float gpms = (float) indiceStats.get().getCount() / Math.max(1, indiceStats.get().getTimeInMillis());

				collector.recordMetric("indices.search.query_per_second", qpms * 1000);
				collector.recordMetric("indices.search,query_time_millis", 1 / qpms);
				collector.recordMetric("indices.search.fetch_per_second", fpms * 1000);
				collector.recordResponseTimeMetric("indices.search.fetch_time_millis", (long) (1 / fpms));
				collector.recordMetric("indices.get.get_per_second", gpms * 1000);
				collector.recordResponseTimeMetric("indices.get.get_time_millis", (long) (1 / gpms));
			}

			if (getStats != null) {
				collector.recordMetric("indices.get.exists", getStats.existsCount());
				collector.recordMetric("indices.get.missing", getStats.missingCount());
			}

			if (cacheStats != null) {
				collector.recordMetric("indices.cache.field_evictions", cacheStats.getFieldEvictions());
				collector.recordMetric("indices.cache.filter_evictions", cacheStats.getFilterEvictions());
				collector.recordMetric("indices.cache.filter_count", cacheStats.getFilterCount());
				collector.recordMetric("indices.cache.field_size", cacheStats.getFieldSizeInBytes());
				collector.recordMetric("indices.cache.filter_size", cacheStats.getFilterSizeInBytes());
			}

			if (docStats != null) {
				collector.recordMetric("indices.doc.count", docStats.count());
				collector.recordMetric("indices.doc.deleted", docStats.deleted());
			}
			if (indiceStats.store() != null) {
				collector.recordMetric("indices.store.size", indiceStats.store().sizeInBytes());
			}
			if (indiceStats.indexing() != null) {
				collector.recordMetric("indices.indexing.index_total", indiceStats.indexing().total().indexCount());
				collector.recordResponseTimeMetric("indices.indexing.index_time_millis", indiceStats.indexing().total().indexTimeInMillis());
				collector.recordMetric("indices.indexing.delete_total", indiceStats.indexing().total().deleteCount());
				collector.recordResponseTimeMetric("indices.indexing.delete_time_millis", indiceStats.indexing().total().deleteTimeInMillis());
			}
		}

	}

}
