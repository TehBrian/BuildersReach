package xyz.tehbrian.buildersreach.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.tehbrian.buildersreach.BuildersReach;

import java.nio.file.Path;

/**
 * Guice module which provides bindings for the plugin's instances.
 */
public final class PluginModule extends AbstractModule {

    private final BuildersReach buildersReach;

    /**
     * @param buildersReach BuildersReach reference
     */
    public PluginModule(final @NonNull BuildersReach buildersReach) {
        this.buildersReach = buildersReach;
    }

    @Override
    protected void configure() {
        this.bind(BuildersReach.class).toInstance(this.buildersReach);
        this.bind(JavaPlugin.class).toInstance(this.buildersReach);
    }

    /**
     * Provides the plugin's Log4J logger.
     *
     * @return the plugin's Log4J logger
     */
    @Provides
    public @NonNull Logger provideLog4JLogger() {
        return this.buildersReach.getLog4JLogger();
    }

    /**
     * Provides the plugin's data folder.
     *
     * @return the data folder
     */
    @Provides
    @Named("dataFolder")
    public @NonNull Path provideDataFolder() {
        return this.buildersReach.getDataFolder().toPath();
    }

}
