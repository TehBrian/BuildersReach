package xyz.tehbrian.buildersreach.highlight;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import xyz.tehbrian.buildersreach.BuildersReach;
import xyz.tehbrian.buildersreach.util.TeamUtils;

public class ShulkerHighlighter implements Highlighter {

    // Stolen and slightly modified from https://www.spigotmc.org/threads/how-to-make-blocks-glow-no-enchantment.336667/
    public void highlight(Player p, Location loc, int lifetime, ChatColor color) {
        EntityShulker entity = new EntityShulker(EntityTypes.SHULKER, ((CraftWorld) loc.getWorld()).getHandle());
        entity.setLocation(Math.round(loc.getX()) + 0.5, loc.getY(), Math.round(loc.getZ()) + 0.5, 0, 0);

        entity.setInvisible(true);
        entity.setInvulnerable(true);
        entity.getBukkitEntity().setGlowing(true);
        entity.setNoAI(true);

        final Team team = TeamUtils.getColoredTeam(color);
        team.addEntry(entity.getUniqueID().toString());

        EntityPlayer ePlayer = ((CraftPlayer) p).getHandle();
        PlayerConnection connection = ePlayer.playerConnection;

        PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entity);
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
