package xyz.tehbrian.buildersreach.config;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.tehbrian.buildersreach.BuildersReach;

// I'm sure there are better ways of doing this.
// But, this is quick, easy, and it works for now.
public class Options {

    private static BuildersReach main;

    public static final String MAX_REACH_DISTANCE_PATH = "options.max_reach_distance";

    public static int maxReachDistance;

    public static void init(BuildersReach main) {
        Options.main = main;
        loadValues();
    }

    public static void reload() {
        loadValues();
    }

    private static void loadValues() {
        FileConfiguration config = main.getConfig();

        maxReachDistance = config.getInt(MAX_REACH_DISTANCE_PATH);
    }
}
