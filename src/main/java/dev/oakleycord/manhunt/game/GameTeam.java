package dev.oakleycord.manhunt.game;

import org.bukkit.scoreboard.Team;

public enum GameTeam {
    HUNTERS,
    RUNNERS,
    SPECTATORS;

    public Team getTeam(MHGame game) {
        switch (this) {
            case HUNTERS:
                return game.getHunters();
            case RUNNERS:
                return game.getRunners();
            case SPECTATORS:
                return game.getSpectators();
        }
        return null;
    }

    public GameTeam getOpponent() {
        return switch (this) {
            case HUNTERS -> RUNNERS;
            case RUNNERS -> HUNTERS;
            case SPECTATORS -> SPECTATORS;
        };
    }
}
