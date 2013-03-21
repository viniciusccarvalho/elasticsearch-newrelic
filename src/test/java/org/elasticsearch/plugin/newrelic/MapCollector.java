package org.elasticsearch.plugin.newrelic;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.plugin.newrelic.collector.MetricCollector;

public class MapCollector implements MetricCollector{
	
	private Map<String,Number> stats = new HashMap<String, Number>();
	
	@Override
	public void recordMetric(String name, Number value) {
		if(value != null){
			stats.put(name, value);
		}
	}

	@Override
	public void recordResponseTimeMetric(String name, long millis) {
		stats.put(name, millis);		
	}

	public Map<String, Number> getStats() {
		return stats;
	}

}
