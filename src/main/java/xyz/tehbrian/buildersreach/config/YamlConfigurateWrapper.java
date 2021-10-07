package xyz.tehbrian.buildersreach.config;

import dev.tehbrian.tehlib.core.configurate.ConfigurateWrapper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

public class YamlConfigurateWrapper extends ConfigurateWrapper<YamlConfigurationLoader> {

    /**
     * @param filePath the file path for the config
     */
    public YamlConfigurateWrapper(@NonNull final Path filePath) {
        super(filePath, YamlConfigurationLoader.builder()
                .path(filePath)
                .build());
    }

    /**
     * @param filePath the file path for the config
     * @param loader   the loader
     */
    public YamlConfigurateWrapper(@NonNull final Path filePath, @NonNull final YamlConfigurationLoader loader) {
        super(filePath, loader);
    }

}
