package org.elasticsearch.river.newrelic;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.river.River;

public class NewRelicRiverModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(River.class).to(NewRelicRiver.class).asEagerSingleton();
	}

}
