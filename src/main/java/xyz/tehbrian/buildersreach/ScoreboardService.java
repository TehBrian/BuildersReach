package xyz.tehbrian.buildersreach;

import com.google.inject.Inject;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ScoreboardService {

    private final BuildersReach buildersReach;
    private final Logger logger;

    @Inject
    public ScoreboardService(
            final @NonNull BuildersReach buildersReach,
            final @NonNull Logger logger
    ) {
        this.buildersReach = buildersReach;
        this.logger = logger;
    }

    /**
     * Returns a {@link Team} that, if joined, will cause the joined entity to glow in the {@code color} supplied.
     *
     * @param color the color
     * @return a team
     */
    public @NonNull Team getColoredTeam(final NamedTextColor color) {
        final Scoreboard board = this.buildersReach.getServer().getScoreboardManager().getMainScoreboard();

        @Nullable Team team = board.getTeam("br_" + color);
        if (team == null) {
            team = board.registerNewTeam("br_" + color);
            team.color(color);
        }

        return team;
    }

    public void deleteColoredTeams() {
        final Scoreboard board = this.buildersReach.getServer().getScoreboardManager().getMainScoreboard();

        for (final Team team : board.getTeams()) {
            if (team.getName().startsWith("br_")) {
                this.logger.info("Deleted {}", team.getName());
                team.unregister();
            }
        }
    }

}
