package xyz.tehbrian.buildersreach.listeners;

import com.destroystokyo.paper.block.TargetBlockInfo;
import com.google.inject.Inject;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import xyz.tehbrian.buildersreach.user.User;
import xyz.tehbrian.buildersreach.user.UserService;

import java.util.Objects;

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

        final Block targetBlock = player.getTargetBlockExact(user.reachDistance(), FluidCollisionMode.NEVER);
        if (targetBlock == null) {
            return;
        }

        switch (event.getAction()) {
            case LEFT_CLICK_AIR -> player.breakBlock(targetBlock);
            case RIGHT_CLICK_AIR -> this.rightClick(
                    player,
                    targetBlock,
                    player.getTargetBlockFace(user.reachDistance(), TargetBlockInfo.FluidMode.NEVER),
                    event.getInteractionPoint(),
                    event.getHand(),
                    event.getItem()
            );
            default -> {
            }
        }
    }

    private boolean rightClick(
            final Player player, final Block block,
            final BlockFace blockFace, final Location interactionPoint, final EquipmentSlot equipmentSlot, final ItemStack heldItem
    ) {
        if (interactionPoint != null) {
            return false; // this should be handled by regular ol' block-clickin
            // interaction point is the thing we need to fake if it it's null
        }

        final Location fakePoint = this.pointBetween(player.getLocation(), block.getLocation(), 1);

        final EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        final WorldServer nmsWorld = nmsPlayer.getWorldServer();
        final net.minecraft.world.item.ItemStack nmsHeldItem = CraftItemStack.asNMSCopy(heldItem);
        final EnumHand nmsHand = equipmentSlot == EquipmentSlot.HAND ? EnumHand.a : EnumHand.b;

        final EnumDirection side = Objects.requireNonNull(EnumDirection.a(blockFace.toString().toLowerCase()));

        final EnumInteractionResult result = nmsPlayer.d.a(nmsPlayer, nmsWorld, nmsHeldItem, nmsHand,
                MovingObjectPositionBlock.a(
                        new Vec3D(fakePoint.getX(), fakePoint.getY(), fakePoint.getZ()),
                        side,
                        new BlockPosition(block.getX(), block.getY(), block.getZ())
                )
        );

        return result == EnumInteractionResult.b;
    }

    private Location pointBetween(final Location a, final Location b, final int distanceFromB) {
        // I'm crud at vector arithmetic, there is 100% a better way to do this, don't judge me
        return b.subtract(a.getDirection().normalize().multiply(distanceFromB));
    }

}
