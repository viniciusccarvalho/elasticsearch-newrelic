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
package org.elasticsearch.plugin.newrelic.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Not complete thread safe, but should suffice for the needs of the plugin.
 * 
 */
public class Metric {
	
	private Double current;
	private Double last;
	private final boolean delta;
	private Double min;
	private Double max;
	private Lock lock = new ReentrantLock();
	
	public Metric(){
		this(0.0);
	}
	
	public Metric (Double current){
		this(current,false);
	}
	
	public Metric(Double current, boolean delta) {
		this.current = current;
		this.delta = delta;
		this.min = current;
		this.max = current;
	}

	public void refresh(Double value){
		try {
			lock.lock();
			last = current;
			current = value;
			if(value < min)
				min = value;
			if(value > max)
				max = value;
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			lock.unlock();
		}
	}
	
	public Double getValue(){
		Double value = null;
		try {
			lock.lock();
			if(delta){
				value = (last == null) ? null : current-last;
			}else{
				value = current;
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			lock.unlock();
		}
		return value;
	}
	
	public Map<String, Object> getValues(){
		Map<String,Object> values = new HashMap<String, Object>();
		try {
			lock.lock();
			values.put("current",current);
			values.put("last", last);
			values.put("min", min);
			values.put("max", max);
			values.put("delta", delta);
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			lock.unlock();
		}
		return values;
	}
	
}
