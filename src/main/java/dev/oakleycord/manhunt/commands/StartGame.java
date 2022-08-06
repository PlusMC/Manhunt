package dev.oakleycord.manhunt.commands;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.ManHunt;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.bukkit.managed.PlusCommand;

import java.util.Collections;
import java.util.List;

public class StartGame implements PlusCommand {

    @Override
    public String getName() {
        return "startGame";
    }

    @Override
    public String getPermission() {
        return "manhunt.startGame";
    }

    @Override
    public String getUsage() {
        return "startGame";
    }

    @Override
    public String getDescription() {
        return "Starts the game";
    }

    @Override
    public List<String> getCompletions(int index) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        AbstractRun game = SpeedRuns.getGame();
        if (SpeedRuns.hasGame() && game.getState() == GameState.PREGAME) {
            game.startGame();
            if (game instanceof ManHunt manHunt)
                manHunt.getHunters().getEntries().forEach(hunter -> {
                    Player player = Bukkit.getPlayer(hunter);
                    if (player != null) player.getInventory().addItem(new ItemStack(Material.COMPASS));
                });
            sender.sendMessage("Game Started");
        }
        return true;
    }
}
