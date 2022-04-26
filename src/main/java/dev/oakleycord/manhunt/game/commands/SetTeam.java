package dev.oakleycord.manhunt.game.commands;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.managed.PlusCommand;
import org.plusmc.pluslib.util.BukkitUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SetTeam implements PlusCommand {
    @Override
    public String getName() {
        return "setTeam";
    }

    @Override
    public String getPermission() {
        return "manhunt.setTeam";
    }

    @Override
    public String getUsage() {
        return "setSpectator <player> <team>";
    }

    @Override
    public String getDescription() {
        return "Sets a player to a team";
    }

    @Override
    public List<String> getCompletions(int index) {
        return switch (index) {
            case 1 -> BukkitUtil.allPlayers();
            case 2 -> Arrays.stream(GameTeam.values()).map(Enum::name).collect(Collectors.toList());
            default -> null;
        };
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 2) {
            return false;
        }

        if (ManHunt.GAME == null) {
            sender.sendMessage("§cThere is no game running!");
            return true;
        }
        Player p = Bukkit.getPlayer(args[0]);
        if (p == null) {
            sender.sendMessage("§cThat player is not online!");
            return true;
        }
        try {
            GameTeam team = GameTeam.valueOf(args[1].toUpperCase());
            ManHunt.GAME.setTeam(p, team);
            ManHunt.GAME.getScoreboardHandler().update(0);
            sender.sendMessage("§aSet " + p.getName() + " to " + team.name());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cThat team does not exist!");
        }

        return true;
    }
}
