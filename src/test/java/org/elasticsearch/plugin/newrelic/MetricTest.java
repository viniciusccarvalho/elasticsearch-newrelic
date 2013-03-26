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
package org.elasticsearch.plugin.newrelic;

import junit.framework.Assert;

import org.elasticsearch.plugin.newrelic.model.Metric;
import org.junit.Test;

public class MetricTest {
	
	@Test
	public void simpleMetric(){
		Metric m = new Metric();
		m.refresh(1.0);
		Assert.assertEquals(1.0, m.getValue());
		m.refresh(2.0);
		Assert.assertEquals(2.0, m.getValue());
	}
	
	@Test
	public void deltaMetric(){
		Metric m = new Metric(1.0,true);
		Assert.assertEquals(null, m.getValue());
		m.refresh(3.0);
		Assert.assertEquals(2.0, m.getValue());
	}
	
	@Test
	public void deltaMetricValues(){
		Metric m = new Metric(1.0,true);
		Assert.assertEquals(null, m.getValue());
		m.refresh(3.0);
		Assert.assertEquals(2.0, m.getValue());
		
		Assert.assertEquals(1.0, m.getValues().get("min"));
		Assert.assertEquals(3.0, m.getValues().get("max"));
	}
}
