package xyz.tehbrian.buildersreach.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.tehbrian.tehlib.paper.configurate.AbstractLangConfig;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class LangConfig extends AbstractLangConfig<YamlConfigurateWrapper> {

    /**
     * @param dataFolder the data folder
     * @param logger     the logger
     */
    @Inject
    public LangConfig(
            final @NotNull @Named("dataFolder") Path dataFolder,
            final @NotNull Logger logger
    ) {
        super(new YamlConfigurateWrapper(dataFolder.resolve("lang.yml")), logger);
    }

}
