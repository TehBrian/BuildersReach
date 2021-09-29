package xyz.tehbrian.buildersreach.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.tehbrian.tehlib.core.configurate.AbstractConfig;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import xyz.tehbrian.buildersreach.BuildersReach;

import java.nio.file.Path;

/**
 * Loads and holds values for {@code config.yml}.
 */
public final class ConfigConfig extends AbstractConfig<YamlConfigurateWrapper> {

    private final BuildersReach buildersReach;

    private @Nullable Data data;

    @Inject
    public ConfigConfig(
            final @NotNull Logger logger,
            final @NotNull @Named("dataFolder") Path dataFolder,
            final @NotNull BuildersReach buildersReach
    ) {
        super(logger, new YamlConfigurateWrapper(logger, dataFolder.resolve("config.yml"), YamlConfigurationLoader.builder()
                .path(dataFolder.resolve("config.yml"))
                .defaultOptions(opts -> opts.implicitInitialization(false))
                .build()));
        this.buildersReach = buildersReach;
    }

    @Override
    public void load() {
        this.configurateWrapper.load();
        final CommentedConfigurationNode rootNode = this.configurateWrapper.get();
        final String fileName = this.configurateWrapper.filePath().getFileName().toString();

        try {
            this.data = rootNode.get(Data.class);
        } catch (final SerializationException e) {
            this.logger.warn("Exception caught during configuration deserialization for {}", fileName);
            this.logger.warn("Disabling plugin. Please check your {}", fileName);
            this.buildersReach.disableSelf();
            this.logger.warn("Printing stack trace:", e);
            return;
        }

        if (this.data == null) {
            this.logger.warn("The deserialized configuration for {} was null.", fileName);
            this.logger.warn("Disabling plugin. Please check your {}", fileName);
            this.buildersReach.disableSelf();
            return;
        }

        this.logger.info("Successfully loaded configuration file {}", fileName);
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public @Nullable Data data() {
        return this.data;
    }

    @ConfigSerializable
    public static record Data(int maxReachDistance) {

    }

}
