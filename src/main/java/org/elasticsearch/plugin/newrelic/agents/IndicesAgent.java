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
				
				collector.recordMetric("indices.search.total.query_count",searchStats.total().getQueryCount() );
				collector.recordMetric("indices.search.total.query_time_millis",searchStats.total().getQueryTimeInMillis());
				collector.recordMetric("indices.search.queries_per_second", qpms * 1000);
				collector.recordMetric("indices.search.average_query_time_millis",  (qpms == 0 ? 0 : (1.0f / qpms)));
				collector.recordMetric("indices.search.total.fetch_count",searchStats.total().getFetchCount() );
				collector.recordMetric("indices.search.total.fetch_time_millis",searchStats.total().getFetchTimeInMillis());
				collector.recordMetric("indices.search.fetches_per_second", fpms * 1000);
				collector.recordMetric("indices.search.average_fetch_time_millis",  (fpms == 0 ? 0 : (1.0f / fpms)));
				
			}

			if (getStats != null) {
				float gpms = (float) indiceStats.get().getCount() / Math.max(1, indiceStats.get().getTimeInMillis());
				
				collector.recordMetric("indices.get.total.get",getStats.count() );
				collector.recordMetric("indices.get.total.get_time_millis",getStats.getTimeInMillis());
				
				collector.recordMetric("indices.get.exists", getStats.existsCount());
				collector.recordMetric("indices.get.missing", getStats.missingCount());
				collector.recordMetric("indices.get.gets_per_second", gpms * 1000);
				collector.recordMetric("indices.get.average_get_time_millis", (gpms == 0 ? 0 : (1.0f / gpms)));
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
				collector.recordMetric("indices.indexing.index_time_millis", indiceStats.indexing().total().indexTimeInMillis());
				collector.recordMetric("indices.indexing.delete_total", indiceStats.indexing().total().deleteCount());
				collector.recordMetric("indices.indexing.delete_time_millis", indiceStats.indexing().total().deleteTimeInMillis());
			}
		}

	}

}
