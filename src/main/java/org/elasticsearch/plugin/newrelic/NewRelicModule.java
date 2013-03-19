package org.elasticsearch.plugin.newrelic;

import org.elasticsearch.common.inject.AbstractModule;

public class NewRelicModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(NewRelicNodeAgent.class).asEagerSingleton();
	}

}
