package xyz.tehbrian.buildersreach.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import xyz.tehbrian.buildersreach.BuildersReach;
import xyz.tehbrian.buildersreach.config.Message;
import xyz.tehbrian.buildersreach.config.Options;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BuildersReachCommand implements CommandExecutor, TabCompleter {

    private final BuildersReach main;

    public BuildersReachCommand(BuildersReach main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;

            if (main.getPlayerDataManager().togglePlayerEnabled(player)) {
                player.sendMessage(Message.ENABLED.getValue());
            } else {
                player.sendMessage(Message.DISABLED.getValue());
            }

            return true;
        }

        switch (args[0].toLowerCase(Locale.ENGLISH)) {
            case "reload": {
                main.reloadConfig();
                Options.reload();
                sender.sendMessage(Message.RELOAD.getValue());
                break;
            }
            case "distance": {
                if (!(sender instanceof Player)) return true;
                Player player = (Player) sender;

                if (args.length >= 2) {
                    int distance;
                    try {
                        distance = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(Message.INVALID_DISTANCE.getValue());
                        return true;
                    }
                    if (distance <= 0) {
                        player.sendMessage(Message.INVALID_DISTANCE.getValue());
                        return true;
                    }
                    if (distance > Options.maxReachDistance) {
                        player.sendMessage(Message.OVER_MAX_DISTANCE.getValue());
                        return true;
                    }

                    main.getPlayerDataManager().setReachDistance(player, distance);
                    player.sendMessage(Message.SET_DISTANCE.getValue(distance));
                }

                break;
            }
            case "color": {
                if (!(sender instanceof Player)) return true;
                Player player = (Player) sender;

                if (args.length >= 2) {
                    ChatColor color;
                    try {
                        color = ChatColor.valueOf(args[1]);
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(Message.INVALID_COLOR.getValue());
                        return true;
                    }

                    main.getPlayerDataManager().setHighlightColor(player, color);
                    player.sendMessage(Message.SET_COLOR.getValue(color.name()));
                }

                break;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "distance", "color");
        }
        if (args.length == 2) {
            if (args[0].toLowerCase(Locale.ENGLISH).equals("color")) {
                return Arrays.stream(ChatColor.values()).map(ChatColor::name).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }
}
