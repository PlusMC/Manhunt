package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.game.ManHuntGame;
import org.plusmc.pluslib.managed.Tickable;

public class GameLoop implements Tickable {

    private final ManHuntGame game;

    public GameLoop(ManHuntGame game) {
        this.game = game;
    }

    @Override
    public void tick(long tick) {
        if (game.getState() != ManHuntGame.GameState.INGAME) return;
        game.getScoreboardHandler().updateScoreboard(tick);
        game.getCompassHandler().updateCompass(tick);


        if (game.getRunners().getSize() == 0)
            game.postGame(ManHuntGame.GameTeam.HUNTERS);
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }
}
