package dev.oakleycord.manhunt.game.commands;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.gui.MHSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.bukkit.managed.PlusCommand;

import java.util.Collections;
import java.util.List;

public class GameSettings implements PlusCommand {

    @Override
    public String getName() {
        return "gameSettings";
    }

    @Override
    public String getPermission() {
        return "manhunt.gamesettings";
    }

    @Override
    public String getUsage() {
        return "gameSettings";
    }

    @Override
    public String getDescription() {
        return "Opens the game settings menu";
    }

    @Override
    public List<String> getCompletions(int index) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        AbstractRun game = SpeedRuns.getGame();
        if (game == null) {
            sender.sendMessage("Game not initialized");
            return false;
        }
        if (sender instanceof HumanEntity he) he.openInventory(new MHSettings(game).getInventory());
        else sender.sendMessage("You must be a player to use this command!");
        return true;
    }
}
