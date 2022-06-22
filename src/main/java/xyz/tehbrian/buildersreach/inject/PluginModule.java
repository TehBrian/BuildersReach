package xyz.tehbrian.buildersreach.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import xyz.tehbrian.buildersreach.BuildersReach;

import java.nio.file.Path;

public final class PluginModule extends AbstractModule {

    private final BuildersReach buildersReach;

    public PluginModule(final @NonNull BuildersReach buildersReach) {
        this.buildersReach = buildersReach;
    }

    @Override
    protected void configure() {
        this.bind(BuildersReach.class).toInstance(this.buildersReach);
        this.bind(JavaPlugin.class).toInstance(this.buildersReach);
    }

    /**
     * @return the plugin's SLF4J logger
     */
    @Provides
    public @NonNull Logger provideSLF4JLogger() {
        return this.buildersReach.getSLF4JLogger();
    }

    /**
     * @return the plugin's data folder
     */
    @Provides
    @Named("dataFolder")
    public @NonNull Path provideDataFolder() {
        return this.buildersReach.getDataFolder().toPath();
    }

}
