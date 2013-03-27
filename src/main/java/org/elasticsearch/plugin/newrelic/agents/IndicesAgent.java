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
import org.elasticsearch.plugin.newrelic.model.Metric;

public class IndicesAgent extends NodeAgent  {

	public IndicesAgent() {
		
		metrics.put("indices/search/total",new Metric("indices/search/total"));
		metrics.put("indices/search/time_millis",new Metric(0.0,true,"indices/search/time_millis"));
		metrics.put("indices/fetch/total",new Metric("indices/fetch/total"));
		metrics.put("indices/fetch/time_millis",new Metric(0.0,true,"indices/fetch/time_millis"));
		metrics.put("indices/get/total",new Metric("indices/get/total"));
		metrics.put("indices/get/time_millis",new Metric(0.0,true,"indices/get/time_millis"));
		metrics.put("indices/get/exists",new Metric(0.0,true,"indices/get/exists"));
		metrics.put("indices/get/missing",new Metric(0.0,true,"indices/get/missing"));
		metrics.put("indices/cache/filter_count",new Metric("indices/cache/filter_count"));
		metrics.put("indices/cache/filter_size",new Metric("indices/cache/filter_size"));
		metrics.put("indices/cache/field_size",new Metric("indices/cache/field_size"));
		metrics.put("indices/cache/field_evictions",new Metric(0.0,true,"indices/cache/field_evictions"));
		metrics.put("indices/cache/filter_evictions",new Metric(0.0,true,"indices/cache/filter_evictions"));
		metrics.put("indices/doc/total",new Metric("indices/doc/total"));
		metrics.put("indices/doc/deleted",new Metric("indices/doc/deleted"));
		metrics.put("indices/store/size",new Metric("indices/store/size"));
		metrics.put("indices/indexing/total",new Metric(0.0,true,"indices/indexing/total"));
		metrics.put("indices/indexing/time_millis",new Metric(0.0,true,"indices/indexing/time_millis"));
		metrics.put("indices/delete/total",new Metric(0.0,true,"indices/delete/total"));
		metrics.put("indices/delete/time_millis",new Metric(0.0,true,"indices/delete/time_millis"));
	}
	

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
				collector.recordMetric(metrics.get("indices/search/total").refresh(searchStats.total().getQueryCount() ));
				collector.recordMetric(metrics.get("indices/search/time_millis").refresh(searchStats.total().getQueryTimeInMillis()));
				collector.recordMetric(metrics.get("indices/fetch/total").refresh(searchStats.total().getFetchCount()) );
				collector.recordMetric(metrics.get("indices/fetch/time_millis").refresh(searchStats.total().getFetchTimeInMillis()));
			}
			if (getStats != null) {
				collector.recordMetric(metrics.get("indices/get/total").refresh(getStats.count()));
				collector.recordMetric(metrics.get("indices/get/time_millis").refresh(getStats.getTimeInMillis()));
				collector.recordMetric(metrics.get("indices/get/exists").refresh( getStats.existsCount()));
				collector.recordMetric(metrics.get("indices/get/missing").refresh( getStats.missingCount()));
			}
			if (cacheStats != null) {
				collector.recordMetric(metrics.get("indices/cache/field_evictions").refresh(cacheStats.getFieldEvictions()));
				collector.recordMetric(metrics.get("indices/cache/field_evictions").refresh( cacheStats.getFilterEvictions()));
				collector.recordMetric(metrics.get("indices/cache/filter_count").refresh( cacheStats.getFilterCount()));
				collector.recordMetric(metrics.get("indices/cache/field_size").refresh( cacheStats.getFieldSizeInBytes()));
				collector.recordMetric(metrics.get("indices/cache/filter_size").refresh( cacheStats.getFilterSizeInBytes()));
			}
			if (docStats != null) {
				collector.recordMetric(metrics.get("indices/doc/count").refresh( docStats.count()));
				collector.recordMetric(metrics.get("indices/doc/deleted").refresh( docStats.deleted()));
			}
			if (indiceStats.store() != null) {
				collector.recordMetric(metrics.get("indices/store/size").refresh( indiceStats.store().sizeInBytes()));
			}
			if (indiceStats.indexing() != null) {
				collector.recordMetric(metrics.get("indices/indexing/total").refresh( indiceStats.indexing().total().indexCount()));
				collector.recordMetric(metrics.get("indices/indexing/time_millis").refresh( indiceStats.indexing().total().indexTimeInMillis()));
				collector.recordMetric(metrics.get("indices/delete/total").refresh( indiceStats.indexing().total().deleteCount()));
				collector.recordMetric(metrics.get("indices/delete/time_millis").refresh( indiceStats.indexing().total().deleteTimeInMillis()));
			}
		}		
	}

	@Override
	public String getName() {
		return "indices";
	}

}
