package org.elasticsearch.plugin.river.newrelic;

import java.util.ArrayList;
import java.util.Collection;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.river.newrelic.NewRelicModule;

public class NewRelicPlugin extends AbstractPlugin {

	@Inject
	public NewRelicPlugin(){}
	
	private final ESLogger logger = ESLoggerFactory.getLogger(this.getClass().getName());
	
	public String description() {
		return "Newrelic agent plugin";
	}

	public String name() {
		return "agent-newrelic";
	}


	@Override
	public Collection<Class<? extends Module>> modules() {
		logger.debug("NewRelic plugin registering newrelic module");
		Collection<Class<? extends Module>> modules = new ArrayList();
		modules.add(NewRelicModule.class);
		return modules;
	}
	

}
