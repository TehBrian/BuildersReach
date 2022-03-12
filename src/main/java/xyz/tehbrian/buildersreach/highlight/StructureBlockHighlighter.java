package xyz.tehbrian.buildersreach.highlight;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class StructureBlockHighlighter implements Highlighter {

    private final Map<UUID, Location> lastLocations = new HashMap<>();
    private final Vector sub = new Vector(0, -10, 0); // hehe

    public void highlight(final Player p, final Location loc, final int lifetime, final NamedTextColor color) {
        final @Nullable Location lastLocation = this.lastLocations.get(p.getUniqueId());
        if (lastLocation != null && lastLocation.equals(loc)) {
            return;
        }
        this.lastLocations.put(p.getUniqueId(), loc);

        final ServerPlayer ePlayer = ((CraftPlayer) p).getHandle();
        final ServerPlayerConnection connection = ePlayer.connection;

        final Location subLoc = loc.clone().subtract(this.sub);
        final BlockPos subLocPos = new BlockPos(subLoc.getX(), subLoc.getY(), subLoc.getZ());

        final ClientboundBlockUpdatePacket addStructureBlock = new ClientboundBlockUpdatePacket(
                subLocPos,
                Blocks.STRUCTURE_BLOCK.defaultBlockState()
        );

        final CompoundTag nbtData;
        try {
            nbtData = TagParser.parseTag(
                    "{\"x\":_x_,\"y\":_y_,\"z\":_z_,\"posX\":0,\"posY\":-10,\"posZ\":0,\"sizeX\":1,\"sizeY\":1,\"sizeZ\":1,\"mirror\":\"NONE\",\"powered\":0,\"seed\":[0,0],\"integrity\":1,\"showboundingbox\":1,\"showair\":0,\"name\":\"minecraft:br_highlighter\",\"rotation\":\"NONE\",\"mode\":\"SAVE\",\"id\":\"minecraft:structure_block\",\"author\":_author_,\"metadata\":\"\",\"ignoreEntities\":1}"
                            .replace("_x_", String.valueOf(subLoc.getBlockX()))
                            .replace("_y_", String.valueOf(subLoc.getBlockY()))
                            .replace("_z_", String.valueOf(subLoc.getBlockZ()))
                            .replace("_author_", p.getName()));
        } catch (final CommandSyntaxException e) {
            e.printStackTrace();
            return;
        }

        final ClientboundBlockEntityDataPacket sendStructureData = ClientboundBlockEntityDataPacket.create(
                new StructureBlockEntity(subLocPos, Blocks.STRUCTURE_BLOCK.defaultBlockState()),
                entity -> nbtData
        );

        if (lastLocation != null) {
            final Location subLastLoc = lastLocation.clone().subtract(this.sub);
            final BlockPos subLastLocPos = new BlockPos(subLastLoc.getBlockX(), subLastLoc.getBlockY(), subLastLoc.getBlockZ());

            final ClientboundBlockUpdatePacket removeOldBlock = new ClientboundBlockUpdatePacket(
                    subLastLocPos,
                    Blocks.AIR.defaultBlockState()
            );

            connection.send(removeOldBlock);
        }

        connection.send(addStructureBlock);
        connection.send(sendStructureData);
    }

}
