package dev.oakleycord.manhunt.game.commands;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.logic.modes.Mode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.managed.PlusCommand;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SetGameMode implements PlusCommand {

    @Override
    public String getName() {
        return "setGameMode";
    }

    @Override
    public String getPermission() {
        return "manhunt.setGameMode";
    }

    @Override
    public String getUsage() {
        return "setGameMode <mode>";
    }

    @Override
    public String getDescription() {
        return "Sets the gamemode of the game";
    }

    @Override
    public List<String> getCompletions(int index) {
        if (index == 1) return Arrays.stream(Mode.values()).map(Mode::name).collect(Collectors.toList());
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) return false;
        if (ManHunt.GAME == null) {
            sender.sendMessage("There is no game running");
            return true;
        }
        try {
            Mode mode = Mode.valueOf(args[0].toUpperCase());
            ManHunt.GAME.setGameMode(mode);
            ManHunt.GAME.getScoreboardHandler().update(0);
            sender.sendMessage("Game mode set to " + mode.name());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Invalid gamemode");
            return true;
        }
        return true;
    }
}
