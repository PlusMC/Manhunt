package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.GameState;
import org.bukkit.Bukkit;
import org.plusmc.pluslib.bukkit.managed.Tickable;

public class GameLoop implements Tickable {

    private final AbstractRun game;

    public GameLoop(AbstractRun game) {
        this.game = game;
    }

    @Override
    public void tick(long tick) {
        game.updateVariables();
        if (game.getState() != GameState.INGAME) return;
        game.tick(tick);

        if (game.getGameModeLogic() != null) {
            game.getGameModeLogic().getTimings().startTiming();
            game.getGameModeLogic().tick(tick);
            game.getGameModeLogic().getTimings().stopTiming();
            game.getModifierLogic().forEach(modifier -> {
                modifier.getTimings().startTiming();
                modifier.tick(tick);
                modifier.getTimings().stopTiming();
            });
        } else {
            Bukkit.broadcastMessage("ERROR RUNNING GAMEMODE ENDING GAME...");
            game.postGame();
        }
    }
}
