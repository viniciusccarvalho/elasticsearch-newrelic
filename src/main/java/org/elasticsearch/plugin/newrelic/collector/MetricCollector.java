package org.elasticsearch.plugin.newrelic.collector;

/**
 * 
 * @author vinicius
 * 
 * Why would we need this? Well, newrelic horrible final static API is not mockable ...
 *
 */
public interface MetricCollector {
	
	public void recordMetric(String name, Number value);
	public void recordResponseTimeMetric(String name, long millis);
	
}
