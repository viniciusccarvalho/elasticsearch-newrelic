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
import org.elasticsearch.index.cache.CacheStats;
import org.elasticsearch.index.get.GetStats;
import org.elasticsearch.index.search.stats.SearchStats;
import org.elasticsearch.index.shard.DocsStats;
import org.elasticsearch.indices.NodeIndicesStats;

public class IndicesAgent extends NodeAgent  {

	public IndicesAgent() {
		this.fieldEvictions = new AtomicLong(Long.MAX_VALUE);
		this.filterEvictions = new AtomicLong(Long.MAX_VALUE);
	}
	
	private AtomicLong fieldEvictions;
	private AtomicLong filterEvictions;

	@Override
	public void execute(NodeStats nodeStats) {
		NodeIndicesStats indiceStats = nodeStats.getIndices();
		if (indiceStats != null) {
			logger.debug("Running IndicesAgent");
			SearchStats searchStats = indiceStats.getSearch();
			GetStats getStats = indiceStats.get();
			CacheStats cacheStats = indiceStats.cache();
			DocsStats docStats = indiceStats.docs();

			if (searchStats != null) {
				float qpms = (float) searchStats.total().getQueryCount() / Math.max(1, searchStats.total().getQueryTimeInMillis());
				float fpms = (float) searchStats.total().getFetchCount() / Math.max(1, searchStats.total().getFetchTimeInMillis());
				
				collector.recordMetric("indices/search/total",searchStats.total().getQueryCount() );
				collector.recordMetric("indices/search/time_millis",searchStats.total().getQueryTimeInMillis());
				collector.recordMetric("indices/search/per_second", qpms * 1000);
				collector.recordMetric("indices/search/average_time_millis",  (qpms == 0 ? 0 : (1.0f / qpms)));
				collector.recordMetric("indices/fetch/total",searchStats.total().getFetchCount() );
				collector.recordMetric("indices/fetch/time_millis",searchStats.total().getFetchTimeInMillis());
				collector.recordMetric("indices/fetch/per_second", fpms * 1000);
				collector.recordMetric("indices/fetch/average_time_millis",  (fpms == 0 ? 0 : (1.0f / fpms)));
				
			}

			if (getStats != null) {
				float gpms = (float) indiceStats.get().getCount() / Math.max(1, indiceStats.get().getTimeInMillis());
				
				collector.recordMetric("indices/get/total",getStats.count() );
				collector.recordMetric("indices/get/time_millis",getStats.getTimeInMillis());
				
				collector.recordMetric("indices/get/exists", getStats.existsCount());
				collector.recordMetric("indices/get/missing", getStats.missingCount());
				collector.recordMetric("indices/get/per_second", gpms * 1000);
				collector.recordMetric("indices/get/average_time_millis", (gpms == 0 ? 0 : (1.0f / gpms)));
			}

			if (cacheStats != null) {
				
				if((cacheStats.getFieldEvictions() - fieldEvictions.get()) >= 0){
					collector.recordMetric("indices/cache/field_evictions", (cacheStats.getFieldEvictions() - fieldEvictions.get()));
				}
				if((cacheStats.getFilterEvictions() - filterEvictions.get()) >= 0){
					collector.recordMetric("indices/cache/field_evictions", (cacheStats.getFilterEvictions() - filterEvictions.get()));
				}
				collector.recordMetric("indices/cache/filter_count", cacheStats.getFilterCount());
				collector.recordMetric("indices/cache/field_size", cacheStats.getFieldSizeInBytes());
				collector.recordMetric("indices/cache/filter_size", cacheStats.getFilterSizeInBytes());
				fieldEvictions.set(cacheStats.getFieldEvictions());
				filterEvictions.set(cacheStats.getFilterEvictions());
			}

			if (docStats != null) {
				collector.recordMetric("indices/doc/count", docStats.count());
				collector.recordMetric("indices/doc/deleted", docStats.deleted());
			}
			if (indiceStats.store() != null) {
				collector.recordMetric("indices/store/size", indiceStats.store().sizeInBytes());
			}
			if (indiceStats.indexing() != null) {
				collector.recordMetric("indices/indexing/total", indiceStats.indexing().total().indexCount());
				collector.recordMetric("indices/indexing/time_millis", indiceStats.indexing().total().indexTimeInMillis());
				collector.recordMetric("indices/delete/total", indiceStats.indexing().total().deleteCount());
				collector.recordMetric("indices/delete/time_millis", indiceStats.indexing().total().deleteTimeInMillis());
			}
		}		
	}

	@Override
	public String getName() {
		return "indices";
	}

}
