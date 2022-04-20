package dev.oakleycord.manhunt.game.logic.gamemodes;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.enums.GameTeam;
import dev.oakleycord.manhunt.game.logic.Logic;

public class Classic extends Logic {

    public Classic(MHGame game) {
        super(game);
    }

    public void update(long tick) {
        MHGame game = getGame();
        if (game.getRunners().getSize() == 0)
            game.postGame(GameTeam.HUNTERS);
        else if (game.hasDragonBeenKilled())
            game.postGame(GameTeam.RUNNERS);
    }
}
