package org.elasticsearch.plugin.newrelic.collector;

import com.newrelic.api.agent.NewRelic;

public class NewRelicCollector implements MetricCollector {

	@Override
	public void recordMetric(String name, Number value) {
		if(value != null){
			NewRelic.recordMetric(name, value.floatValue());
		}
	}

	@Override
	public void recordResponseTimeMetric(String name, long millis) {
		NewRelic.recordResponseTimeMetric(name, millis);
	}

}
