package dev.oakleycord.manhunt.game.commands;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.MHGame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.bukkit.managed.PlusCommand;

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
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        MHGame game = ManHunt.getGame();
        if (ManHunt.hasGame() && game.getState() == GameState.PREGAME) {
            game.startGame();
            game.getHunters().getEntries().forEach(hunter -> {
                Player player = Bukkit.getPlayer(hunter);
                if (player != null) player.getInventory().addItem(new ItemStack(Material.COMPASS));
            });
            sender.sendMessage("Game Started");
        }
        return true;
    }
}
