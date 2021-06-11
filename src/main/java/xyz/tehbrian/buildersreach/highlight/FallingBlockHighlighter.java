package xyz.tehbrian.buildersreach.highlight;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import xyz.tehbrian.buildersreach.BuildersReach;
import xyz.tehbrian.buildersreach.util.TeamUtils;

public class FallingBlockHighlighter implements Highlighter {

    // Stolen and modified from https://www.spigotmc.org/threads/1-15-invisible-shulker.412945/
    // TODO This could be optimized by not deleting the block every tick.
    public void highlight(Player p, Location loc, int lifetime, ChatColor color) {
        IBlockData data = CraftMagicNumbers.getBlock(Material.ORANGE_WOOL).getBlockData();
        EntityFallingBlock entity = new EntityFallingBlock(
                ((CraftWorld) loc.getWorld()).getHandle(),
                loc.getX() + 0.5,
                loc.getY(),
                loc.getZ() + 0.5,
                data);

        entity.setInvisible(true);
        entity.setInvulnerable(true);
        entity.getBukkitEntity().setGlowing(true);
        entity.setNoGravity(true);

        final Team team = TeamUtils.getColoredTeam(color);
        team.addEntry(entity.getUniqueID().toString());

        EntityPlayer ePlayer = ((CraftPlayer) p).getHandle();
        PlayerConnection connection = ePlayer.playerConnection;

        PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(entity, net.minecraft.server.v1_16_R3.Block.getCombinedId(data));
        PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(entity.getBukkitEntity().getEntityId(), entity.getDataWatcher(), true);

        connection.sendPacket(spawnPacket);
        connection.sendPacket(metaPacket);

        Bukkit.getScheduler().scheduleSyncDelayedTask(BuildersReach.getInstance(), () -> {
            PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entity.getId());
            connection.sendPacket(destroyPacket);
            team.removeEntry(entity.getUniqueID().toString());
        }, lifetime);
    }
}
