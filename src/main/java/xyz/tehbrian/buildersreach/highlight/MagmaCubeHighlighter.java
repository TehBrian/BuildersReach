package xyz.tehbrian.buildersreach.highlight;

import com.google.inject.Inject;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityMagmaCube;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import xyz.tehbrian.buildersreach.BuildersReach;
import xyz.tehbrian.buildersreach.ScoreboardService;

public final class MagmaCubeHighlighter implements Highlighter {

    private final BuildersReach buildersReach;
    private final ScoreboardService scoreboardService;

    @Inject
    public MagmaCubeHighlighter(
            final BuildersReach buildersReach,
            final ScoreboardService scoreboardService
    ) {
        this.buildersReach = buildersReach;
        this.scoreboardService = scoreboardService;
    }

    // https://www.spigotmc.org/threads/how-to-make-blocks-glow-no-enchantment.336667/
    public void highlight(final Player p, final Location loc, final int lifetime, final NamedTextColor color) {
        final EntityMagmaCube entity = new EntityMagmaCube(EntityTypes.X, ((CraftWorld) loc.getWorld()).getHandle());
        entity.setLocation(Math.round(loc.getX()) + 0.5, loc.getY(), Math.round(loc.getZ()) + 0.5, 0, 0);

        entity.setInvisible(true);
        entity.setInvulnerable(true);
        entity.setNoGravity(true);
        entity.getBukkitEntity().setGlowing(true);
        entity.setNoAI(true);
        entity.setSize(2, true);

        final Team team = this.scoreboardService.getColoredTeam(color);
        team.addEntry(entity.getUniqueID().toString());

        final EntityPlayer ePlayer = ((CraftPlayer) p).getHandle();
        final PlayerConnection connection = ePlayer.b;

        final PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entity);
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
