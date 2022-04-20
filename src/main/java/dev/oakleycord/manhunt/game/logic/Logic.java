package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.game.MHGame;

public abstract class Logic {
    private final MHGame game;

    public Logic(MHGame game) {
        this.game = game;
    }

    public MHGame getGame() {
        return game;
    }

    public abstract void update(long tick);
}
