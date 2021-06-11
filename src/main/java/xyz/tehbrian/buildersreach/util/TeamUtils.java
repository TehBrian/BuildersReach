package xyz.tehbrian.buildersreach.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

public class TeamUtils {

    /**
     * Returns a {@link Team} that, if joined, will cause the joined entity to glow in the {@code color} supplied.
     *
     * @param color the color wanted
     * @return a team
     */
    public static Team getColoredTeam(ChatColor color) {
        org.bukkit.scoreboard.Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam("br_color_" + color.getChar());
        if (team == null) {
            team = board.registerNewTeam("br_color_" + color.getChar());
            team.setColor(color);
        }
        return team;
    }
}
