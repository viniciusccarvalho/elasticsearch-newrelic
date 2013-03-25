package org.elasticsearch.plugin.newrelic.rest.action;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.plugin.newrelic.Configuration;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestRequest.Method;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.rest.XContentRestResponse;
import org.elasticsearch.rest.action.support.RestXContentBuilder;

public class NewRelicAction extends BaseRestHandler {

	@Inject
	public NewRelicAction(Settings settings, Client client, RestController controller) {
		super(settings, client);
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
				builder.field("configuration");
				builder.map(Configuration.getInstance().getData());
			builder.endObject();
			channel.sendResponse(new XContentRestResponse(request, RestStatus.OK, builder));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void handlePost(RestRequest request, RestChannel channel) {
		
	}
	
}
