package dev.oakleycord.manhunt.game.boards;

import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.ManhuntPublic;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

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
        if (game.getState() == GameState.INGAME)
            return super.getEntries(tick);

        List<String> entries = new ArrayList<>();
        entries.add("");
        entries.add("Players: §b%playerAmount%/%maxPlayers%");

        if (!game.isStarting())
            entries.add("Starting in: §b%startTime%");
        else entries.add("Waiting for players for at least 2 players");


        entries.add("");

        entries.add("§8§lGameState: %gameState%");
        return entries;
    }

    @Override
    public boolean useVariables() {
        return true;
    }

}
