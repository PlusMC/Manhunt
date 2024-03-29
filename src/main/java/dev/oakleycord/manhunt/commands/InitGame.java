package dev.oakleycord.manhunt.commands;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.bukkit.managed.PlusCommand;

import java.util.Collections;
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
    public List<String> getCompletions(int index) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (SpeedRuns.hasGame()) {
            sender.sendMessage("Game already initialized");
            return false;
        }

        SpeedRuns.createGame();
        AbstractRun game = SpeedRuns.getGame();
        game.pregame();
        for (Player player : Bukkit.getOnlinePlayers()) {
            World world = game.getWorldHandler().getWorldOverworld();
            world.getSpawnLocation().getChunk().load();
            player.teleport(world.getSpawnLocation().add(0, 1, 0));
        }

        return true;
    }
}
