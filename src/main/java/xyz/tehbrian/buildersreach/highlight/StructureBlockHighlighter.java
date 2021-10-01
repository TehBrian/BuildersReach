package xyz.tehbrian.buildersreach.highlight;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
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

        final EntityPlayer ePlayer = ((CraftPlayer) p).getHandle();
        final PlayerConnection connection = ePlayer.b;

        final Location subLoc = loc.clone().subtract(this.sub);
        final BlockPosition subLocPos = new BlockPosition(subLoc.getX(), subLoc.getY(), subLoc.getZ());

        final PacketPlayOutBlockChange addStructureBlock = new PacketPlayOutBlockChange(
                subLocPos,
                CraftMagicNumbers.getBlock(Material.STRUCTURE_BLOCK).getBlockData()
        );

        final NBTTagCompound nbtData;
        try {
            nbtData = MojangsonParser.parse(
                    "{\"x\":_x_,\"y\":_y_,\"z\":_z_,\"posX\":0,\"posY\":-10,\"posZ\":0,\"sizeX\":1,\"sizeY\":1,\"sizeZ\":1,\"mirror\":\"NONE\",\"powered\":0,\"seed\":[0,0],\"integrity\":1,\"showboundingbox\":1,\"showair\":0,\"name\":\"minecraft:br_highlighter\",\"rotation\":\"NONE\",\"mode\":\"SAVE\",\"id\":\"minecraft:structure_block\",\"author\":_author_,\"metadata\":\"\",\"ignoreEntities\":1}"
                            .replace("_x_", String.valueOf(subLoc.getBlockX()))
                            .replace("_y_", String.valueOf(subLoc.getBlockY()))
                            .replace("_z_", String.valueOf(subLoc.getBlockZ()))
                            .replace("_author_", p.getName()));
        } catch (final CommandSyntaxException e) {
            e.printStackTrace();
            return;
        }

        final PacketPlayOutTileEntityData sendStructureData = new PacketPlayOutTileEntityData(
                subLocPos,
                7,
                nbtData
        );

        if (lastLocation != null) {
            final Location subLastLoc = lastLocation.clone().subtract(this.sub);
            final BlockPosition subLastLocPos = new BlockPosition(subLastLoc.getBlockX(), subLastLoc.getBlockY(), subLastLoc.getBlockZ());

            final PacketPlayOutBlockChange removeOldBlock = new PacketPlayOutBlockChange(
                    subLastLocPos,
                    CraftMagicNumbers.getBlock(Material.AIR).getBlockData()
            );

            connection.sendPacket(removeOldBlock);
        }

        connection.sendPacket(addStructureBlock);
        connection.sendPacket(sendStructureData);
    }

}
