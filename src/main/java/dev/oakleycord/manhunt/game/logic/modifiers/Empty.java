package dev.oakleycord.manhunt.game.logic.modifiers;

import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.logic.Logic;

public class Empty extends Logic {

    public Empty(AbstractRun game) {
        super(game);
    }

    @Override
    public void tick(long tick) {
        // Do nothing lollll
    }
}