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

import java.util.concurrent.atomic.AtomicLong;

import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.index.cache.filter.FilterCacheStats;
import org.elasticsearch.index.get.GetStats;
import org.elasticsearch.index.search.stats.SearchStats;
import org.elasticsearch.index.shard.DocsStats;
import org.elasticsearch.indices.NodeIndicesStats;

public class IndicesAgent extends NodeAgent  {

	public IndicesAgent() {
		this.filterEvictions = new AtomicLong(Long.MAX_VALUE);
	}
	
	private AtomicLong filterEvictions;

	@Override
	public void execute(NodeStats nodeStats) {
		NodeIndicesStats indiceStats = nodeStats.getIndices();
		if (indiceStats != null) {
			logger.debug("Running IndicesAgent");
			SearchStats searchStats = indiceStats.getSearch();
			GetStats getStats = indiceStats.getGet();
			FilterCacheStats filterCacheStats = indiceStats.getFilterCache();
			DocsStats docStats = indiceStats.getDocs();

			if (searchStats != null) {
				float qpms = (float) searchStats.getTotal().getQueryCount() / Math.max(1, searchStats.getTotal().getQueryTimeInMillis());
				float fpms = (float) searchStats.getTotal().getFetchCount() / Math.max(1, searchStats.getTotal().getFetchTimeInMillis());
				
				collector.recordMetric("indices/search/total",searchStats.getTotal().getQueryCount() );
				collector.recordMetric("indices/search/time_millis",searchStats.getTotal().getQueryTimeInMillis());
				collector.recordMetric("indices/search/per_second", qpms * 1000);
				collector.recordMetric("indices/search/average_time_millis",  (qpms == 0 ? 0 : (1.0f / qpms)));
				collector.recordMetric("indices/fetch/total",searchStats.getTotal().getFetchCount() );
				collector.recordMetric("indices/fetch/time_millis",searchStats.getTotal().getFetchTimeInMillis());
				collector.recordMetric("indices/fetch/per_second", fpms * 1000);
				collector.recordMetric("indices/fetch/average_time_millis",  (fpms == 0 ? 0 : (1.0f / fpms)));
				
			}

			if (getStats != null) {
				float gpms = (float) indiceStats.getGet().getCount() / Math.max(1, indiceStats.getGet().getTimeInMillis());
				
				collector.recordMetric("indices/get/total",getStats.getCount() );
				collector.recordMetric("indices/get/time_millis",getStats.getTimeInMillis());
				
				collector.recordMetric("indices/get/exists", getStats.getExistsCount());
				collector.recordMetric("indices/get/missing", getStats.getMissingCount());
				collector.recordMetric("indices/get/per_second", gpms * 1000);
				collector.recordMetric("indices/get/average_time_millis", (gpms == 0 ? 0 : (1.0f / gpms)));
			}

			if (filterCacheStats != null) {

				if((filterCacheStats.getEvictions() - filterEvictions.get()) >= 0){
					collector.recordMetric("indices/cache/filter_evictions", (filterCacheStats.getEvictions() - filterEvictions.get()));
				}
				collector.recordMetric("indices/cache/filter_size", filterCacheStats.getMemorySizeInBytes());
				filterEvictions.set(filterCacheStats.getEvictions());
			}

			if (docStats != null) {
				collector.recordMetric("indices/doc/count", docStats.getCount());
				collector.recordMetric("indices/doc/deleted", docStats.getDeleted());
			}
			if (indiceStats.getStore() != null) {
				collector.recordMetric("indices/store/size", indiceStats.getStore().sizeInBytes());
			}
			if (indiceStats.getIndexing() != null) {
				collector.recordMetric("indices/indexing/total", indiceStats.getIndexing().getTotal().getIndexCount());
				collector.recordMetric("indices/indexing/time_millis", indiceStats.getIndexing().getTotal().getIndexTimeInMillis());
				collector.recordMetric("indices/delete/total", indiceStats.getIndexing().getTotal().getDeleteCount());
				collector.recordMetric("indices/delete/time_millis", indiceStats.getIndexing().getTotal().getDeleteTimeInMillis());
			}
		}		
	}

	@Override
	public String getName() {
		return "indices";
	}

}
