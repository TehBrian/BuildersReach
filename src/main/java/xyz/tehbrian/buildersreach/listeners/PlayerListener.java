package xyz.tehbrian.buildersreach.listeners;

import com.google.inject.Inject;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerInteractManager;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
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

//        final ItemStack itemStack = new ItemStack(CraftMagicNumbers.getBlock(Material.GLASS), 1);
//        final WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
        final EntityPlayer playerHandle = ((CraftPlayer) player).getHandle();
        final PlayerInteractManager d = playerHandle.d;

        switch (event.getAction()) {
            case LEFT_CLICK_AIR -> d.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
            case RIGHT_CLICK_AIR -> block.setType(player.getInventory().getItemInMainHand().getType());
            //d.a(playerHandle, world, itemStack, EnumHand.b);
            default -> {
            }
        }
    }

}
