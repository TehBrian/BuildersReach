package xyz.tehbrian.buildersreach.highlight;

import com.google.inject.Inject;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.MagmaCube;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
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
        final MagmaCube entity = new MagmaCube(EntityType.MAGMA_CUBE, ((CraftWorld) loc.getWorld()).getHandle());
        entity.setPos(Math.round(loc.getX()) + 0.5, loc.getY(), Math.round(loc.getZ()) + 0.5);
        entity.setRot(0, 0);

        entity.setInvisible(true);
        entity.setInvulnerable(true);
        entity.setNoGravity(true);
        entity.getBukkitEntity().setGlowing(true);
        entity.setNoAi(true);
        entity.setSize(2, true);

        final Team team = this.scoreboardService.getColoredTeam(color);
        team.addEntry(entity.getUUID().toString());

        final ServerPlayer ePlayer = ((CraftPlayer) p).getHandle();
        final ServerPlayerConnection connection = ePlayer.connection;

        final ClientboundAddMobPacket spawnPacket = new ClientboundAddMobPacket(entity);
        final ClientboundSetEntityDataPacket metaPacket = new ClientboundSetEntityDataPacket(
                entity.getBukkitEntity().getEntityId(),
                entity.getEntityData(),
                true
        );

        connection.send(spawnPacket);
        connection.send(metaPacket);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.buildersReach, () -> {
            final ClientboundRemoveEntitiesPacket destroyPacket = new ClientboundRemoveEntitiesPacket(entity.getId());
            connection.send(destroyPacket);
            team.removeEntry(entity.getUUID().toString());
        }, lifetime);
    }

}
