package xyz.tehbrian.buildersreach.highlight;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Highlighter {

    /**
     * Highlights a block location for a specific player via a glowing entity outline.
     *
     * @param p        the player
     * @param loc      the location
     * @param lifetime the lifetime of the glowing block, in ticks
     * @param color    the color of the glow
     */
    void highlight(Player p, Location loc, int lifetime, ChatColor color);
}
