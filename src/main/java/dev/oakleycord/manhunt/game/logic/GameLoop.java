package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.plusmc.pluslib.bukkit.managed.Tickable;

public class GameLoop implements Tickable {

    private final AbstractRun game;
    private int keepAlive;

    public GameLoop(AbstractRun game) {
        this.game = game;
        this.keepAlive = SpeedRuns.getKeepAliveTimeout();
    }

    @Override
    public void tick(long tick) {
        game.updateVariables();
        game.tick(tick);
        if (game.getState() != GameState.INGAME) return;

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
            Bukkit.broadcastMessage(ChatColor.RED + "ERROR RUNNING GAMEMODE ENDING GAME...");
            game.postGame();
        }

        if (!SpeedRuns.useKeepAlive())
            return;

        if (game.getPlayers(false).size() >  1)
            keepAlive = SpeedRuns.getKeepAliveTimeout();

        if (keepAlive == SpeedRuns.getKeepAliveTimeout() - 1)
            Bukkit.broadcastMessage(ChatColor.AQUA + "The game will end in " + ChatColor.GOLD + (keepAlive / 20) + ChatColor.AQUA + " seconds if no players join back.");

        if (keepAlive > 0) {
            keepAlive--;
        } else {
            SpeedRuns.getInstance().getLogger().info("Keep Alive Expired, Ending Game...");
            game.destroy();
        }
    }
}
