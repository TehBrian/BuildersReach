package xyz.tehbrian.buildersreach;

import com.google.inject.Inject;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ScoreboardService {

    private final BuildersReach buildersReach;

    @Inject
    public ScoreboardService(final @NonNull BuildersReach buildersReach) {
        this.buildersReach = buildersReach;
    }

    /**
     * Returns a {@link Team} that, if joined, will cause the joined entity to glow in the {@code color} supplied.
     *
     * @param color the color
     * @return a team
     */
    public @NonNull Team getColoredTeam(final NamedTextColor color) {
        final Scoreboard board = this.buildersReach.getServer().getScoreboardManager().getMainScoreboard();

        @Nullable Team team = board.getTeam("br_color_" + color.asHSV());
        if (team == null) {
            team = board.registerNewTeam("br_color_" + color.asHSV());
            team.color(color);
        }

        return team;
    }

}
