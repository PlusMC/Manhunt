package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.game.MHGame;

public abstract class Logic {
    private final MHGame game;

    protected Logic(MHGame game) {
        this.game = game;
    }

    public MHGame getGame() {
        return game;
    }

    protected abstract void update(long tick);
}
