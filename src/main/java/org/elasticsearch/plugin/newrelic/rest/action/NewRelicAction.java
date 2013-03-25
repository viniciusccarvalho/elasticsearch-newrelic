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
package org.elasticsearch.plugin.newrelic.rest.action;

import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.plugin.newrelic.Configuration;
import org.elasticsearch.plugin.newrelic.NewRelicNodeAgent;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestRequest.Method;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.rest.XContentRestResponse;
import org.elasticsearch.rest.action.support.RestXContentBuilder;

public class NewRelicAction extends BaseRestHandler {
	
	private NewRelicNodeAgent nodeAgent;
	
	
	@Inject
	public NewRelicAction(Settings settings, Client client, RestController controller, NewRelicNodeAgent nodeAgent) {
		super(settings, client);
		this.nodeAgent = nodeAgent;
		controller.registerHandler(Method.GET, "/_newrelic", this);
		controller.registerHandler(Method.POST, "/_newrelic", this);
	}

	@Override
	public void handleRequest(RestRequest request, RestChannel channel) {
		switch (request.method()) {
			
			case GET:
				handleGet(request, channel);
			break;

			case POST:
				handlePost(request, channel);
			break;
			
			default:
			break;	
		}
	
	}
	
	
	public void handleGet(RestRequest request, RestChannel channel) {
		try {
			XContentBuilder builder = RestXContentBuilder.restContentBuilder(request);
			builder.startObject();
				builder.startObject("configuration");
					builder.startObject("agents");
						Map<String,Boolean> nodeStates = nodeAgent.agentState();
						for(String nodeName :nodeStates.keySet()){
							builder.field(nodeName,nodeStates.get(nodeName));
						}
					builder.endObject();
					Map<String,Object> config = Configuration.getInstance().getData();
					for(String field : config.keySet()){
						builder.field(field,config.get(field));
					}
				builder.endObject();	
			builder.endObject();
			channel.sendResponse(new XContentRestResponse(request, RestStatus.OK, builder));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void handlePost(RestRequest request, RestChannel channel) {
		Map<String,Boolean> agentState = nodeAgent.agentState();
		if(request.param("all") != null){
			Boolean state = request.paramAsBoolean("all", Boolean.FALSE);
			for(String agent: agentState.keySet()){
				nodeAgent.setState(agent, state);
			}
		}else{
			for(String agentName : agentState.keySet()){
				if(request.param(agentName) != null){
					nodeAgent.setState(agentName, request.paramAsBoolean(agentName, Boolean.FALSE));
				}
			}
		}
		
		if(request.param("refreshInterval") != null)
			Configuration.getInstance().put("refreshInterval", request.paramAsLong("refreshInterval", 10L));
		
		try {
			XContentBuilder builder = RestXContentBuilder.restContentBuilder(request);
			channel.sendResponse(new XContentRestResponse(request, RestStatus.OK, builder));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
