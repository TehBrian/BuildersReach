package xyz.tehbrian.buildersreach.config;

import dev.tehbrian.tehlib.core.configurate.AbstractConfig;
import dev.tehbrian.tehlib.core.configurate.ConfigurateWrapper;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public abstract class AbstractDataConfig<W extends ConfigurateWrapper<?>, D> extends AbstractConfig<W> {

    protected @Nullable D data;

    /**
     * @param logger             the logger
     * @param configurateWrapper the wrapper
     */
    public AbstractDataConfig(@NonNull final Logger logger, @NonNull final W configurateWrapper) {
        super(logger, configurateWrapper);
    }

    @Override
    public void load() {
        this.configurateWrapper.load();
        final CommentedConfigurationNode rootNode = this.configurateWrapper.get();
        final String fileName = this.configurateWrapper.filePath().getFileName().toString();

        try {
            this.setData(rootNode);
        } catch (final SerializationException e) {
            this.logger.warn("Exception caught during configuration deserialization for {}", fileName);
            this.logger.warn("Disabling plugin. Please check your {}", fileName);
            this.disablePlugin();
            this.logger.warn("Printing stack trace:", e);
            return;
        }

        if (this.data == null) {
            this.logger.warn("The deserialized configuration for {} was null.", fileName);
            this.logger.warn("Disabling plugin. Please check your {}", fileName);
            this.disablePlugin();
            return;
        }

        this.logger.info("Successfully loaded configuration file {}", fileName);
    }

    protected abstract void disablePlugin();

    protected abstract void setData(CommentedConfigurationNode rootNode) throws SerializationException;

    /**
     * Gets the data.
     *
     * @return the data
     */
    public @Nullable D data() {
        return this.data;
    }

}
