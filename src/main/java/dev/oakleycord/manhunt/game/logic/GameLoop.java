package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.GameTeam;
import dev.oakleycord.manhunt.game.MHGame;
import org.bukkit.Bukkit;
import org.plusmc.pluslib.managed.Tickable;

public class GameLoop implements Tickable {

    private final MHGame game;

    public GameLoop(MHGame game) {
        this.game = game;
    }

    @Override
    public void tick(long tick) {
        if (game.getState() != GameState.INGAME) return;
        game.getScoreboardHandler().tick(tick);
        game.getCompassHandler().tick(tick);

        if (game.getGameModeLogic() != null) {
            game.getGameModeLogic().tick(tick);
            for (Logic modifiers : game.getModifierLogic()) modifiers.tick(tick);
        } else {
            Bukkit.broadcastMessage("ERROR RUNNING GAMEMODE ENDING GAME...");
            game.postGame(GameTeam.SPECTATORS);
        }
    }
}
