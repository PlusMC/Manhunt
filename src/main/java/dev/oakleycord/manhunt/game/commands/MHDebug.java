package dev.oakleycord.manhunt.game.commands;

import dev.oakleycord.manhunt.ManHunt;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.managed.PlusCommand;

import java.util.List;

public class MHDebug implements PlusCommand {


    @Override
    public String getName() {
        return "MHDebug";
    }

    @Override
    public String getPermission() {
        return "manhunt.debug";
    }

    @Override
    public String getUsage() {
        return "MHDebug <option>";
    }

    @Override
    public String getDescription() {
        return "Helpful for debugging idk lol";
    }

    @Override
    public JavaPlugin getPlugin() {
        return ManHunt.getInstance();
    }

    @Override
    public List<String> getCompletions(int index) {
        return List.of("tpWorld");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return false;
        if (!(sender instanceof Player p)) return false;
        if (ManHunt.GAME == null) return false;

        switch (args[0]) {
            case "tpWorld" -> p.teleport(ManHunt.GAME.getOverworld().getSpawnLocation());
        }
        return true;
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }
}
