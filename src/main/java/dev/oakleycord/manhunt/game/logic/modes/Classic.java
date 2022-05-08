package dev.oakleycord.manhunt.game.logic.modes;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.GameTeam;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class Classic extends Logic {

    private final KillListener killListener;

    public Classic(MHGame game) {
        super(game);
        killListener = new KillListener();
    }

    public void tick(long tick) {
        MHGame game = getGame();
        if (game.getRunners().getSize() == 0)
            game.postGame(GameTeam.HUNTERS);
    }

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(killListener, ManHunt.getInstance());
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(killListener);
    }

    private class KillListener implements Listener {

        @EventHandler
        public void onEntityDeath(EntityDeathEvent event) {
            if (!OtherUtil.isManHunt(event.getEntity().getWorld())) return;
            MHGame game = getGame();
            if (game.getState() != GameState.INGAME) return;
            if (event.getEntityType() != EntityType.ENDER_DRAGON) return;

            game.postGame(GameTeam.RUNNERS);
        }
    }
}
