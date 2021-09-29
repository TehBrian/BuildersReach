package xyz.tehbrian.buildersreach.inject;

import com.google.inject.AbstractModule;
import xyz.tehbrian.buildersreach.config.ConfigConfig;
import xyz.tehbrian.buildersreach.config.LangConfig;

/**
 * Guice module which provides the various configs.
 */
public class ConfigModule extends AbstractModule {

    /**
     * Binds the configs as eager singletons.
     */
    @Override
    protected void configure() {
        this.bind(ConfigConfig.class).asEagerSingleton();
        this.bind(LangConfig.class).asEagerSingleton();
    }

}
