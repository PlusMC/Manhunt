package dev.oakleycord.manhunt.game.commands;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.ManHuntGame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.managed.PlusCommand;

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
    public JavaPlugin getPlugin() {
        return ManHunt.getInstance();
    }

    @Override
    public List<String> getCompletions(int index) {
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (ManHunt.GAME != null && ManHunt.GAME.getState() == ManHuntGame.GameState.PREGAME) {
            ManHunt.GAME.resume();
            ManHunt.GAME.getHunters().getEntries().forEach(hunter -> {
                Player player = Bukkit.getPlayer(hunter);
                if (player != null) player.getInventory().addItem(new ItemStack(Material.COMPASS));
            });
            sender.sendMessage("Game Started");
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
