package xyz.tehbrian.buildersreach.listeners;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.tehbrian.buildersreach.BuildersReach;
import xyz.tehbrian.buildersreach.data.PlayerDataManager;

public class PlayerListener implements Listener {

    private final BuildersReach main;

    public PlayerListener(BuildersReach main) {
        this.main = main;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        PlayerDataManager playerDataManager = main.getPlayerDataManager();
        Player player = event.getPlayer();

        if (!playerDataManager.isPlayerEnabled(player)) return;

        Block block = player.getTargetBlockExact(playerDataManager.getReachDistance(player), FluidCollisionMode.SOURCE_ONLY);
        if (block == null) return;

        switch (event.getAction()) {
            case LEFT_CLICK_AIR: {
                block.setType(Material.AIR);
                break;
            }
            case RIGHT_CLICK_AIR: {
                block.setType(player.getInventory().getItemInMainHand().getType());
                break;
            }
        }
    }
}
