package xyz.tehbrian.buildersreach.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.tehbrian.tehlib.core.configurate.AbstractDataConfig;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

/**
 * Loads and holds values for {@code config.yml}.
 */
public final class ConfigConfig extends AbstractDataConfig<YamlConfigurateWrapper, ConfigConfig.Data> {

    @Inject
    public ConfigConfig(final @NotNull @Named("dataFolder") Path dataFolder) {
        super(new YamlConfigurateWrapper(dataFolder.resolve("config.yml"), YamlConfigurationLoader.builder()
                .path(dataFolder.resolve("config.yml"))
                .defaultOptions(opts -> opts.implicitInitialization(false))
                .build()));
    }

    @Override
    protected Class<Data> getDataClass() {
        return Data.class;
    }

    @ConfigSerializable
    public record Data(int maxReachDistance,
                       Highlighter highlighter,
                       String fallingBlockType
    ) {

        public enum Highlighter {
            FALLING_BLOCK,
            MAGMA_CUBE,
            SHULKER,
            STRUCTURE_BLOCK
        }

    }

}
