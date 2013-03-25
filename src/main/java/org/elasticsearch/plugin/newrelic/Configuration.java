package org.elasticsearch.plugin.newrelic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {
	
	private Map<String, Object> data;
	
	private static Configuration instance;
	
	private Configuration(){
		this.data = new ConcurrentHashMap<String, Object>();
	}

	public static Configuration getInstance(){
		if(instance == null){
			synchronized (Configuration.class) {
				instance = new Configuration();
			}
		}
		return instance;
	}

	public Object get(Object key) {
		return data.get(key);
	}

	public Object put(String key, Object value) {
		return data.put(key, value);
	}

	public Map<String, Object> getData() {
		return Collections.unmodifiableMap(data);
	}
	
	
}
