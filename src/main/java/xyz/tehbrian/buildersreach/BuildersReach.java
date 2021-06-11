package xyz.tehbrian.buildersreach;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.tehbrian.buildersreach.commands.BuildersReachCommand;
import xyz.tehbrian.buildersreach.config.Options;
import xyz.tehbrian.buildersreach.highlight.MagmaCubeHighlighter;
import xyz.tehbrian.buildersreach.listeners.PlayerListener;
import xyz.tehbrian.buildersreach.data.PlayerDataManager;
import xyz.tehbrian.buildersreach.highlight.BlockHighlightingTask;

public final class BuildersReach extends JavaPlugin {

    private static BuildersReach instance;

    private PlayerDataManager playerDataManager;

    public BuildersReach() {
        instance = this;
    }

    public static BuildersReach getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        setupConfig();
        setupCommands();
        setupListeners();
        setupTasks();
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }

    public void setupConfig() {
        saveDefaultConfig();
        Options.init(this);
    }

    public void setupCommands() {
        BuildersReachCommand mainCommand = new BuildersReachCommand(this);
        getCommand("buildersreach").setExecutor(mainCommand);
        getCommand("buildersreach").setTabCompleter(mainCommand);
    }

    public void setupListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerListener(this), this);
    }

    public void setupTasks() {
        BlockHighlightingTask blockHighlightingTask = new BlockHighlightingTask(this);
        blockHighlightingTask.setHighlighter(new MagmaCubeHighlighter());

        getServer().getScheduler().scheduleSyncRepeatingTask(this, blockHighlightingTask, 1, 1);
    }

    public PlayerDataManager getPlayerDataManager() {
        if (playerDataManager == null) {
            playerDataManager = new PlayerDataManager();
        }
        return playerDataManager;
    }
}
