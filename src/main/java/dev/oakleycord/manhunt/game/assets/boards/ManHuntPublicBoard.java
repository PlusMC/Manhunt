package dev.oakleycord.manhunt.game.assets.boards;

import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.ManhuntPublic;
import org.bukkit.scoreboard.Scoreboard;
import org.plusmc.pluslibcore.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class ManHuntPublicBoard extends ManhuntBoard {
    private final ManhuntPublic game;

    public ManHuntPublicBoard(ManhuntPublic game) {
        super(game);
        this.game = game;
    }

    public ManHuntPublicBoard(ManhuntPublic game, Scoreboard scoreboard) {
        super(game, scoreboard);
        this.game = game;
    }

    @Override
    public List<String> getEntries(long tick) {
        if (game.getState() != GameState.PREGAME)
            return super.getEntries(tick);

        List<String> entries = new ArrayList<>();
        entries.add("");
        entries.add("Players: " + AQUA + "%playerAmount%/%maxPlayers%");

        if (game.isStarting())
            entries.add("Starting in: " + AQUA + "%startTime%");
        else entries.add(GRAY + "Waiting for players...");


        entries.add("");

        //entries.add(DARK_GRAY + "" + BOLD + "GameState: %gameState%");


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
