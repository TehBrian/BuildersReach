package xyz.tehbrian.buildersreach.highlight;

import com.google.inject.Inject;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import xyz.tehbrian.buildersreach.BuildersReach;
import xyz.tehbrian.buildersreach.ScoreboardService;
import xyz.tehbrian.buildersreach.config.ConfigConfig;

public final class FallingBlockHighlighter implements Highlighter {

    private final BuildersReach buildersReach;
    private final ScoreboardService scoreboardService;
    private final ConfigConfig configConfig;

    @Inject
    public FallingBlockHighlighter(
            final ConfigConfig configConfig,
            final BuildersReach buildersReach,
            final ScoreboardService scoreboardService
    ) {
        this.configConfig = configConfig;
        this.buildersReach = buildersReach;
        this.scoreboardService = scoreboardService;
    }

    // https://www.spigotmc.org/threads/1-15-invisible-shulker.412945/
    // TODO: optimize by not deleting the block every tick
    public void highlight(final Player p, final Location loc, final int lifetime, final NamedTextColor color) {
        final BlockState data = CraftMagicNumbers
                .getBlock(Material.valueOf(this.configConfig.data().fallingBlockType()))
                .defaultBlockState();
        final FallingBlockEntity entity = new FallingBlockEntity(
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
        team.addEntry(entity.getUUID().toString());

        final ServerPlayer ePlayer = ((CraftPlayer) p).getHandle();
        final ServerPlayerConnection connection = ePlayer.connection;

        final ClientboundAddEntityPacket spawnPacket = new ClientboundAddEntityPacket(entity, Block.getId(data));
        final ClientboundSetEntityDataPacket metaPacket = new ClientboundSetEntityDataPacket(
                entity.getId(),
                entity.getEntityData().getNonDefaultValues()
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
