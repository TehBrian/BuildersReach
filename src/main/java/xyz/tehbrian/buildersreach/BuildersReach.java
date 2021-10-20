package xyz.tehbrian.buildersreach;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.tehbrian.tehlib.core.configurate.Config;
import dev.tehbrian.tehlib.paper.TehPlugin;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import xyz.tehbrian.buildersreach.command.BuildersReachCommand;
import xyz.tehbrian.buildersreach.command.CommandService;
import xyz.tehbrian.buildersreach.config.ConfigConfig;
import xyz.tehbrian.buildersreach.config.LangConfig;
import xyz.tehbrian.buildersreach.highlight.BlockHighlightingTask;
import xyz.tehbrian.buildersreach.highlight.FallingBlockHighlighter;
import xyz.tehbrian.buildersreach.highlight.Highlighter;
import xyz.tehbrian.buildersreach.highlight.MagmaCubeHighlighter;
import xyz.tehbrian.buildersreach.highlight.ShulkerHighlighter;
import xyz.tehbrian.buildersreach.highlight.StructureBlockHighlighter;
import xyz.tehbrian.buildersreach.inject.ConfigModule;
import xyz.tehbrian.buildersreach.inject.HIghlightingModule;
import xyz.tehbrian.buildersreach.inject.PluginModule;
import xyz.tehbrian.buildersreach.inject.UserModule;
import xyz.tehbrian.buildersreach.listeners.PlayerListener;

import java.util.List;

public final class BuildersReach extends TehPlugin {

    /**
     * The Guice injector.
     */
    private @MonotonicNonNull Injector injector;

    @Override
    public void onEnable() {
        try {
            this.injector = Guice.createInjector(
                    new ConfigModule(),
                    new HIghlightingModule(),
                    new PluginModule(this),
                    new UserModule()
            );
        } catch (final Exception e) {
            this.getLog4JLogger().error("Something went wrong while creating the Guice injector.");
            this.getLog4JLogger().error("Disabling plugin.");
            this.disableSelf();
            this.getLog4JLogger().error("Printing stack trace, please send this to the developers:", e);
            return;
        }

        if (!this.loadConfiguration()) {
            this.disableSelf();
            return;
        }
        this.setupListeners();
        this.setupCommands();
        this.setupTasks();
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        this.injector.getInstance(ScoreboardService.class).deleteColoredTeams();
    }

    /**
     * Loads the plugin's configuration. If an exception is caught, logs the
     * error and returns false.
     *
     * @return whether or not the loading was successful
     */
    public boolean loadConfiguration() {
        this.saveResourceSilently("lang.yml");
        this.saveResourceSilently("config.yml");

        final List<Config> configsToLoad = List.of(
                this.injector.getInstance(LangConfig.class),
                this.injector.getInstance(ConfigConfig.class)
        );

        for (final Config config : configsToLoad) {
            try {
                config.load();
            } catch (final ConfigurateException e) {
                this.getLog4JLogger().error("Exception caught during config load for {}", config.configurateWrapper().filePath());
                this.getLog4JLogger().error("Please check your config.");
                this.getLog4JLogger().error("Printing stack trace:", e);
                return false;
            }
        }

        this.setHighlighter();

        this.getLog4JLogger().info("Successfully loaded configuration.");
        return true;
    }

    public void setupListeners() {
        registerListeners(
                this.injector.getInstance(PlayerListener.class)
        );
    }

    public void setupCommands() {
        final @NonNull CommandService commandService = this.injector.getInstance(CommandService.class);
        commandService.init();

        final cloud.commandframework.paper.@Nullable PaperCommandManager<CommandSender> commandManager = commandService.get();
        if (commandManager == null) {
            this.getLog4JLogger().error("The CommandService was null after initialization!");
            this.getLog4JLogger().error("Disabling plugin.");
            this.disableSelf();
            return;
        }

        this.injector.getInstance(BuildersReachCommand.class).register(commandManager);
    }

    public void setupTasks() {
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, this.injector.getInstance(BlockHighlightingTask.class), 1, 1);
    }

    public void setHighlighter() {
        final var blockHighlightingTask = this.injector.getInstance(BlockHighlightingTask.class);

        final Highlighter highlighter = switch (this.injector.getInstance(ConfigConfig.class).data().highlighter()) {
            case FALLING_BLOCK -> this.injector.getInstance(FallingBlockHighlighter.class);
            case MAGMA_CUBE -> this.injector.getInstance(MagmaCubeHighlighter.class);
            case SHULKER -> this.injector.getInstance(ShulkerHighlighter.class);
            case STRUCTURE_BLOCK -> this.injector.getInstance(StructureBlockHighlighter.class);
        };

        blockHighlightingTask.setHighlighter(highlighter);
    }

}
