package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.GameTeam;
import dev.oakleycord.manhunt.game.MHGame;
import org.bukkit.Bukkit;
import org.plusmc.pluslib.bukkit.managed.Tickable;

public class GameLoop implements Tickable {

    private final MHGame game;

    public GameLoop(MHGame game) {
        this.game = game;
    }

    @Override
    public void tick(long tick) {
        if (game.getState() != GameState.INGAME) return;
        game.getCompassHandler().getTimings().startTiming();
        game.getCompassHandler().tick(tick);
        game.getCompassHandler().getTimings().stopTiming();

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
            game.postGame(GameTeam.SPECTATORS);
        }
    }
}
