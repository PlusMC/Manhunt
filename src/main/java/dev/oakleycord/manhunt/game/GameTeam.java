package dev.oakleycord.manhunt.game;

public enum GameTeam {
    HUNTERS,
    RUNNERS,
    SPECTATORS;

    public GameTeam getOpponent() {
        return switch (this) {
            case HUNTERS -> RUNNERS;
            case RUNNERS -> HUNTERS;
            case SPECTATORS -> SPECTATORS;
        };
    }
}
