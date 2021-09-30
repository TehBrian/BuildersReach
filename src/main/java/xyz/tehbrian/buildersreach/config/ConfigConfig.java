package xyz.tehbrian.buildersreach.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.logging.log4j.Logger;
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
public final class ConfigConfig extends AbstractDataConfig<YamlConfigurateWrapper, ConfigConfig.Data> {

    private final BuildersReach buildersReach;

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
    protected void disablePlugin() {
        this.buildersReach.disableSelf();
    }

    @Override
    protected void setData(final CommentedConfigurationNode rootNode) throws SerializationException {
        this.data = rootNode.get(Data.class);
    }

    @ConfigSerializable
    public static record Data(int maxReachDistance,
                              Highlighter highlighter,
                              String fallingBlockType
    ) {

        public enum Highlighter {
            FALLING_BLOCK,
            MAGMA_CUBE,
            SHULKER
        }

    }

}
