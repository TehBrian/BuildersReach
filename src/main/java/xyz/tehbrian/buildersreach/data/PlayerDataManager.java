package xyz.tehbrian.buildersreach.data;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataManager {

    // Technically, I should be using a PlayerData class instead of sets and
    // hashmaps, but this decision was made for performance reasons.
    //
    // I'll need to iterate over a list of players who have the plugin
    // enabled every tick, and iterating over all PlayerData to compile
    // a set of players who have it enabled seems horribly inefficient.
    private final Set<UUID> enabledPlayers = new HashSet<>();
    private final Map<UUID, Integer> reachDistances = new HashMap<>();
    private final Map<UUID, ChatColor> highlightColors = new HashMap<>();

    public Set<UUID> getEnabledUUIDs() {
        return enabledPlayers;
    }

    public boolean isPlayerEnabled(Player player) {
        return enabledPlayers.contains(player.getUniqueId());
    }

    public void setPlayerEnabled(Player player, boolean bool) {
        UUID uuid = player.getUniqueId();
        if (bool) {
            enabledPlayers.add(uuid);
        } else {
            enabledPlayers.remove(uuid);
        }
    }

    public boolean togglePlayerEnabled(Player player) {
        setPlayerEnabled(player, !isPlayerEnabled(player));
        return isPlayerEnabled(player);
    }

    public int getReachDistance(Player player) {
        return reachDistances.getOrDefault(player.getUniqueId(), 12);
    }

    public void setReachDistance(Player player, int distance) {
        reachDistances.put(player.getUniqueId(), distance);
    }

    public ChatColor getHighlightColor(Player player) {
        return highlightColors.getOrDefault(player.getUniqueId(), ChatColor.WHITE);
    }

    public void setHighlightColor(Player player, ChatColor color) {
        highlightColors.put(player.getUniqueId(), color);
    }
}
