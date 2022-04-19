package dev.oakleycord.manhunt.game.logic.handlers;

import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardHandler extends Logic {

    public ScoreboardHandler(MHGame game) {
        super(game);
        game.getScoreboard().registerNewObjective("manhunt", "dummy", "§6§l§n§oManHunt");
        game.getScoreboard().getObjective("manhunt").setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void update(long tick) {
        MHGame game = getGame();
        if (tick % 10 != 0) return;

        Scoreboard board = game.getScoreboard();
        board.getEntries().forEach(board::resetScores);
        Objective objective = board.getObjective("manhunt");

        objective.getScore("§l").setScore(7);

        objective.getScore("Players: §b" + game.getPlayers().size()).setScore(6);

        objective.getScore("Hunters: §b" + game.getHunters().getSize()).setScore(5);

        objective.getScore("Runners Alive: §b" + game.getRunners().getSize()).setScore(4);

        if (game.getState() == GameState.INGAME)
            objective.getScore("Time: §b" + OtherUtil.formatTime(System.currentTimeMillis() - game.getTimeStamp())).setScore(3);

        objective.getScore("§b").setScore(2);

        objective.getScore("GameMode: §b" + game.getGameMode().name() + "%").setScore(0);

        objective.getScore("§8§lGameState: " + game.getState()).setScore(0);
    }

}
