package xyz.tehbrian.buildersreach.listeners;

import com.google.inject.Inject;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.tehbrian.buildersreach.user.User;
import xyz.tehbrian.buildersreach.user.UserService;

public final class PlayerListener implements Listener {

    private final UserService userService;

    @Inject
    public PlayerListener(final UserService userService) {
        this.userService = userService;
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final User user = this.userService.getUser(player);

        if (!user.enabled()) {
            return;
        }

        final Block block = player.getTargetBlockExact(user.reachDistance(), FluidCollisionMode.SOURCE_ONLY);
        if (block == null) {
            return;
        }

        switch (event.getAction()) {
            case LEFT_CLICK_AIR -> block.setType(Material.AIR);
            case RIGHT_CLICK_AIR -> block.setType(player.getInventory().getItemInMainHand().getType());
            default -> {}
        }
    }

}
