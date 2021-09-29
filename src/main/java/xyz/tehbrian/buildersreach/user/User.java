package xyz.tehbrian.buildersreach.user;

import dev.tehbrian.tehlib.paper.user.PaperUser;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

public final class User extends PaperUser {

    private boolean enabled;
    private int reachDistance = 16;
    private NamedTextColor highlightColor = NamedTextColor.WHITE;

    public User(@NonNull final UUID uuid) {
        super(uuid);
    }

    public boolean enabled() {
        return this.enabled;
    }

    public void enabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean toggleEnabled() {
        this.enabled = !this.enabled;
        return this.enabled;
    }

    public int reachDistance() {
        return this.reachDistance;
    }

    public void reachDistance(final int reachDistance) {
        this.reachDistance = reachDistance;
    }

    public NamedTextColor highlightColor() {
        return this.highlightColor;
    }

    public void highlightColor(final NamedTextColor highlightColor) {
        this.highlightColor = highlightColor;
    }

}
