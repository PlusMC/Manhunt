package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.ManHuntGame;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardHandler {
    public ManHuntGame game;

    public ScoreboardHandler(ManHuntGame game) {
        this.game = game;
        game.getScoreboard().registerNewObjective("manhunt", "dummy", "§6§l§n§oManHunt");
        game.getScoreboard().getObjective("manhunt").setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void updateScoreboard(long tick) {
        if (tick % 10 != 0) return;
        Scoreboard board = game.getScoreboard();
        board.getEntries().forEach(board::resetScores);
        Objective objective = board.getObjective("manhunt");

        objective.getScore("§l").setScore(7);

        objective.getScore("Players: §b" + game.getPlayers().size()).setScore(6);

        objective.getScore("Hunters: §b" + game.getHunters().getSize()).setScore(5);

        objective.getScore("Runners Alive: §b" + game.getRunners().getSize()).setScore(4);

        if (game.getState() == ManHuntGame.GameState.INGAME)
            objective.getScore("Time: §b" + ManHunt.formatTime(System.currentTimeMillis() - game.getTimeStamp())).setScore(3);

        objective.getScore("§b").setScore(2);

        if (game.getState() == ManHuntGame.GameState.POSTGAME)
            objective.getScore("§6§lGame Over!").setScore(1);

        objective.getScore("§8§lGameState: " + game.getState()).setScore(0);
    }

}
