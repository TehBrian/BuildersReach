package xyz.tehbrian.buildersreach.command;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import dev.tehbrian.tehlib.paper.cloud.PaperCloudCommand;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.NodePath;
import xyz.tehbrian.buildersreach.BuildersReach;
import xyz.tehbrian.buildersreach.config.ConfigConfig;
import xyz.tehbrian.buildersreach.config.LangConfig;
import xyz.tehbrian.buildersreach.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class BuildersReachCommand extends PaperCloudCommand<CommandSender> {

    private final BuildersReach buildersReach;
    private final LangConfig langConfig;
    private final UserService userService;
    private final ConfigConfig configConfig;

    @Inject
    public BuildersReachCommand(
            final @NonNull BuildersReach buildersReach,
            final @NonNull LangConfig langConfig,
            final @NonNull UserService userService,
            final @NonNull ConfigConfig configConfig
    ) {
        this.buildersReach = buildersReach;
        this.langConfig = langConfig;
        this.userService = userService;
        this.configConfig = configConfig;
    }

    @Override
    public void register(@NonNull final PaperCommandManager<CommandSender> commandManager) {
        final var main = commandManager.commandBuilder("buildersreach", "br")
                .meta(CommandMeta.DESCRIPTION, "Toggle BuildersReach.")
                .senderType(Player.class)
                .handler(c -> {
                    final var sender = (Player) c.getSender();

                    if (this.userService.getUser(sender).toggleEnabled()) {
                        sender.sendMessage(this.langConfig.c(NodePath.path("enabled")));
                    } else {
                        sender.sendMessage(this.langConfig.c(NodePath.path("disabled")));
                    }
                });

        final var reload = main.literal("reload")
                .handler(c -> {
                    final var sender = c.getSender();

                    if (this.buildersReach.loadConfiguration()) {
                        sender.sendMessage(this.langConfig.c(NodePath.path("reload_successful")));
                    } else {
                        sender.sendMessage(this.langConfig.c(NodePath.path("reload_unsuccessful")));
                    }
                });

        final var distance = main.literal("distance")
                .senderType(Player.class)
                .argument(IntegerArgument.<CommandSender>newBuilder("blocks").withMin(0).build())
                .handler(c -> {
                    final var sender = (Player) c.getSender();
                    final int distanceArgument = c.get("blocks");

                    if (distanceArgument > this.configConfig.data().maxReachDistance()) {
                        sender.sendMessage(this.langConfig.c(NodePath.path("over_max_distance")));
                        return;
                    }

                    this.userService.getUser(sender).reachDistance(distanceArgument);
                    sender.sendMessage(this.langConfig.c(
                            NodePath.path("set_distance"),
                            Map.of("distance", String.valueOf(distanceArgument))
                    ));
                });

        final List<String> namedTextColors = new ArrayList<>(NamedTextColor.NAMES.keys());

        final var color = main.literal("color")
                .senderType(Player.class)
                .argument(StringArgument.<CommandSender>newBuilder("color")
                        .single()
                        .withSuggestionsProvider((c, s) -> namedTextColors)
                        .build())
                .handler(c -> {
                    final var sender = (Player) c.getSender();
                    final String colorArgument = c.get("color");

                    final NamedTextColor namedTextColor = NamedTextColor.NAMES.value(colorArgument);
                    if (namedTextColor == null) {
                        sender.sendMessage(this.langConfig.c(NodePath.path("invalid_color")));
                        return;
                    }

                    this.userService.getUser(sender).highlightColor(namedTextColor);
                    sender.sendMessage(this.langConfig.c(NodePath.path("set_color"), Map.of("color", namedTextColor.toString())));
                });

        commandManager.command(main);
        commandManager.command(reload);
        commandManager.command(distance);
        commandManager.command(color);
    }

}
