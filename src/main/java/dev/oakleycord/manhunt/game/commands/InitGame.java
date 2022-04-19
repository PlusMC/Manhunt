package dev.oakleycord.manhunt.game.commands;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.MHGame;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.managed.PlusCommand;

import java.util.List;

public class InitGame implements PlusCommand {
    @Override
    public String getName() {
        return "initGame";
    }

    @Override
    public String getPermission() {
        return "manhunt.initGame";
    }

    @Override
    public String getUsage() {
        return "initGame";
    }

    @Override
    public String getDescription() {
        return "initializes the world";
    }

    @Override
    public JavaPlugin getPlugin() {
        return ManHunt.getInstance();
    }

    @Override
    public List<String> getCompletions(int index) {
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (ManHunt.GAME != null) {
            sender.sendMessage("Game already initialized");
            return false;
        }

        ManHunt.GAME = new MHGame();
        ManHunt.GAME.pregame();
        for (Player player : Bukkit.getOnlinePlayers()) {
            World world = ManHunt.GAME.getOverworld();
            world.getSpawnLocation().getChunk().load();
            player.teleport(world.getSpawnLocation().add(0, 1, 0));
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
