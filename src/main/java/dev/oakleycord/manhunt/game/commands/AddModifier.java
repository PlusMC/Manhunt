package dev.oakleycord.manhunt.game.commands;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.enums.Modifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.managed.PlusCommand;

import java.util.Arrays;
import java.util.List;

public class AddModifier implements PlusCommand {
    @Override
    public String getName() {
        return "addModifier";
    }

    @Override
    public String getPermission() {
        return "manhunt.modifier.add";
    }

    @Override
    public String getUsage() {
        return "addModifier <modifier>";
    }

    @Override
    public String getDescription() {
        return "Adds a modifier to the game";
    }

    @Override
    public JavaPlugin getPlugin() {
        return ManHunt.getInstance();
    }

    @Override
    public List<String> getCompletions(int index) {
        return Arrays.stream(Modifier.values()).map(Enum::name).toList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) return false;

        if (ManHunt.GAME == null) {
            sender.sendMessage("§cThere is no game running!");
            return true;
        }

        if (!Arrays.stream(Modifier.values()).map(Enum::name).toList().contains(args[0])) {
            sender.sendMessage("§cThat modifier does not exist!");
            return true;
        }

        ManHunt.GAME.getModifiers().add(Modifier.valueOf(args[0].toUpperCase()));
        sender.sendMessage("§aAdded modifier §e" + args[0].toUpperCase() + "§a!");
        ManHunt.GAME.getScoreboardHandler().update(0);
        return true;
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }
}
