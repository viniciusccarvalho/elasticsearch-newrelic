package org.elasticsearch.plugin.river.newrelic;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.river.RiversModule;
import org.elasticsearch.river.newrelic.NewRelicRiverModule;

public class NewRelicPlugin extends AbstractPlugin {

	@Inject
	public NewRelicPlugin(){}
	
	public String description() {
		return "Newrelic agent plugin";
	}

	public String name() {
		return "river-newrelic";
	}
	
	public void onModule(RiversModule module){
		module.registerRiver("newrelic", NewRelicRiverModule.class);
	}

}
