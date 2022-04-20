package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.enums.GameState;
import dev.oakleycord.manhunt.game.enums.GameTeam;
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
        game.getScoreboardHandler().update(tick);
        game.getCompassHandler().update(tick);

        if (game.getGameModeLogic() != null) {
            game.getGameModeLogic().update(tick);
            for (Logic modifiers : game.getModifierLogic()) modifiers.update(tick);
        } else {
            Bukkit.broadcastMessage("ERROR RUNNING GAMEMODE ENDING GAME...");
            game.postGame(GameTeam.SPECTATORS);
        }
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }
}
