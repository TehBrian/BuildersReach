package xyz.tehbrian.buildersreach.highlight;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.tehbrian.buildersreach.user.User;
import xyz.tehbrian.buildersreach.user.UserService;

import java.util.Map;
import java.util.UUID;

public final class BlockHighlightingTask implements Runnable {

    private final UserService userService;

    private Highlighter highlighter;

    @Inject
    public BlockHighlightingTask(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run() {
        for (final Map.Entry<UUID, User> entry : this.userService.getUserMap().entrySet()) {
            final User user = entry.getValue();
            if (!user.enabled()) {
                return;
            }

            final Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) {
                return;
            }

            final Block block = player.getTargetBlockExact(user.reachDistance(), FluidCollisionMode.NEVER);
            if (block == null) {
                return;
            }

            this.highlighter.highlight(player, block.getLocation(), 1, user.highlightColor());
        }
    }

    public void setHighlighter(final Highlighter highlighter) {
        this.highlighter = highlighter;
    }

}
