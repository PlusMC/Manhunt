package dev.oakleycord.manhunt.game.boards;

import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.ManHunt;
import org.bukkit.scoreboard.Scoreboard;
import org.plusmc.pluslib.bukkit.managed.PlusBoard;
import org.plusmc.pluslibcore.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.GOLD;

public class ManhuntBoard extends PlusBoard {
    private final ManHunt game;

    public ManhuntBoard(ManHunt game) {
        super("§6§l§n§oManHunt");
        this.game = game;
    }

    public ManhuntBoard(ManHunt game, Scoreboard scoreboard) {
        super("§6§l§n§oManHunt", scoreboard);
        this.game = game;
    }

    @Override
    public List<String> getEntries(long tick) {
        List<String> entries = new ArrayList<>();
        entries.add("");
        entries.add("Players: §b%playerAmount%");
        entries.add("Hunters: §b%hunterAmount%");
        entries.add("Runners Alive: §b%runnerAmount%");

        if (game.getState() == GameState.INGAME)
            entries.add("Time: §b%time%");

        entries.add("Mode: §b%mode%");

        if (!game.getModifiers().isEmpty())
            entries.add("Modifiers: §b%modifiers%");


        entries.add("");

        //entries.add("§8§lGameState: %gameState%");

        String border = GOLD + MessageUtil.border(entries);
        entries.set(0, border);
        entries.set(entries.size() - 1, border);
        return entries;
    }

    @Override
    public boolean useVariables() {
        return true;
    }

}
