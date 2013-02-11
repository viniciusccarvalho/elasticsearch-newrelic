package org.elasticsearch.plugin.river.newrelic;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.river.RiversModule;
import org.elasticsearch.river.newrelic.NewRelicRiverModule;

public class NewRelicPlugin extends AbstractPlugin {

	@Inject
	public NewRelicPlugin(){}
	
	private final ESLogger logger = ESLoggerFactory.getLogger(this.getClass().getName());
	
	public String description() {
		return "Newrelic agent plugin";
	}

	public String name() {
		return "river-newrelic";
	}
	
	public void onModule(RiversModule module){
		logger.debug("NewRelic plugin registering newrelic module");
		module.registerRiver("newrelic", NewRelicRiverModule.class);
	}

}
