package dev.oakleycord.manhunt.game.logic.modes;

import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.ManHunt;
import dev.oakleycord.manhunt.game.logic.Logic;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class Classic extends Logic {

    private final KillListener killListener;

    public Classic(AbstractRun game) {
        super(game);
        killListener = new KillListener();
    }

    public void tick(long tick) {
    }

    @Override
    public void load() {
        getGame().getWorldHandler().registerEvents(killListener);
    }

    @Override
    public void unload() {
        getGame().getWorldHandler().unregisterEvents(killListener);
    }

    private class KillListener implements Listener {

        @EventHandler
        public void onEntityDeath(EntityDeathEvent event) {
            if (!OtherUtil.isWorldSR(event.getEntity().getWorld())) return;
            AbstractRun game = getGame();
            if (game.getState() != GameState.INGAME) return;
            if (event.getEntityType() != EntityType.ENDER_DRAGON) return;

            if (game instanceof ManHunt manHunt) {
                manHunt.win(ManHunt.MHTeam.RUNNERS);
            } else {
                game.postGame();
            }
        }
    }
}
