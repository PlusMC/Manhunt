package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.game.MHGame;
import org.plusmc.pluslib.bukkit.managed.Tickable;

public abstract class Logic implements Tickable {
    private final MHGame game;

    protected Logic(MHGame game) {
        this.game = game;
    }

    public MHGame getGame() {
        return game; //hi there :)
    }
}
