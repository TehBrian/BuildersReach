package xyz.tehbrian.buildersreach.highlight;

import com.google.inject.Inject;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import xyz.tehbrian.buildersreach.BuildersReach;
import xyz.tehbrian.buildersreach.ScoreboardService;

public final class FallingBlockHighlighter implements Highlighter {

    private final BuildersReach buildersReach;
    private final ScoreboardService scoreboardService;

    @Inject
    public FallingBlockHighlighter(
            final BuildersReach buildersReach,
            final ScoreboardService scoreboardService
    ) {
        this.buildersReach = buildersReach;
        this.scoreboardService = scoreboardService;
    }

    // https://www.spigotmc.org/threads/1-15-invisible-shulker.412945/
    // TODO: optimize by not deleting the block every tick
    public void highlight(final Player p, final Location loc, final int lifetime, final NamedTextColor color) {
        final IBlockData data = CraftMagicNumbers.getBlock(Material.ORANGE_WOOL).getBlockData();
        final EntityFallingBlock entity = new EntityFallingBlock(
                ((CraftWorld) loc.getWorld()).getHandle(),
                loc.getX() + 0.5,
                loc.getY(),
                loc.getZ() + 0.5,
                data
        );

        entity.setInvisible(true);
        entity.setInvulnerable(true);
        entity.getBukkitEntity().setGlowing(true);
        entity.setNoGravity(true);

        final Team team = this.scoreboardService.getColoredTeam(color);
        team.addEntry(entity.getUniqueID().toString());

        final EntityPlayer ePlayer = ((CraftPlayer) p).getHandle();
        final PlayerConnection connection = ePlayer.b;

        final PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(entity, Block.getCombinedId(data));
        final PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(
                entity.getBukkitEntity().getEntityId(),
                entity.getDataWatcher(),
                true
        );

        connection.sendPacket(spawnPacket);
        connection.sendPacket(metaPacket);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.buildersReach, () -> {
            final PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entity.getId());
            connection.sendPacket(destroyPacket);
            team.removeEntry(entity.getUniqueID().toString());
        }, lifetime);
    }

}
