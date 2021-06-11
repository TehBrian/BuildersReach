package xyz.tehbrian.buildersreach.highlight;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.tehbrian.buildersreach.BuildersReach;
import xyz.tehbrian.buildersreach.data.PlayerDataManager;

import java.util.UUID;

public class BlockHighlightingTask implements Runnable {

    private final BuildersReach main;
    private Highlighter highlighter;

    public BlockHighlightingTask(BuildersReach main) {
        this.main = main;
    }

    @Override
    public void run() {
        PlayerDataManager dataManager = main.getPlayerDataManager();

        for (UUID uuid : dataManager.getEnabledUUIDs()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) return;

            Block block = player.getTargetBlockExact(dataManager.getReachDistance(player), FluidCollisionMode.NEVER);
            if (block == null) return;

            highlighter.highlight(player, block.getLocation(), 1, dataManager.getHighlightColor(player));
        }
    }

    public void setHighlighter(Highlighter highlighter) {
        this.highlighter = highlighter;
    }
}
