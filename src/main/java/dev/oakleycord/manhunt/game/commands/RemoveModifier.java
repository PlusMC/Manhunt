package dev.oakleycord.manhunt.game.commands;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.logic.modifiers.Modifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.managed.PlusCommand;

import java.util.List;

public class RemoveModifier implements PlusCommand {
    @Override
    public String getName() {
        return "removeModifier";
    }

    @Override
    public String getPermission() {
        return "manhunt.modifier.remove";
    }

    @Override
    public String getUsage() {
        return "removeModifier <modifier>";
    }

    @Override
    public String getDescription() {
        return "Removes a modifier from the game";
    }

    @Override
    public List<String> getCompletions(int index) {
        if (ManHunt.GAME != null) {
            return ManHunt.GAME.getModifiers().stream().map(Enum::name).toList();
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) return false;

        if (ManHunt.GAME == null) {
            sender.sendMessage("§cThere is no game running!");
            return true;
        }

        if (!ManHunt.GAME.getModifiers().stream().map(Enum::name).toList().contains(args[0])) {
            sender.sendMessage("§cThat modifier does not exist!");
            return true;
        }

        ManHunt.GAME.getModifiers().remove(Modifier.valueOf(args[0]));
        sender.sendMessage("§aRemoved modifier §e" + args[0].toUpperCase() + "§a!");
        ManHunt.GAME.getScoreboardHandler().update(0);
        return true;
    }
}
