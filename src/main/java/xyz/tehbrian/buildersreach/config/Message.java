package xyz.tehbrian.buildersreach.config;

import xyz.tehbrian.buildersreach.BuildersReach;
import xyz.tehbrian.buildersreach.util.MessageUtils;

import java.util.Objects;

public enum Message {
    ENABLED("messages.enabled"),
    DISABLED("messages.disabled"),
    RELOAD("messages.reload"),
    INVALID_DISTANCE("messages.invalid_distance"),
    OVER_MAX_DISTANCE("messages.over_max_distance"),
    SET_DISTANCE("messages.set_distance"),
    INVALID_COLOR("messages.invalid_color"),
    SET_COLOR("messages.set_color");

    private final String configPath;

    Message(String configPath) {
        this.configPath = configPath;
    }

    public String getValue(Object... formats) {
        String rawString = BuildersReach.getInstance().getConfig().getString(configPath);
        Objects.requireNonNull(rawString);

        String formattedString = String.format(rawString, formats);
        return MessageUtils.color(formattedString);
    }
}
