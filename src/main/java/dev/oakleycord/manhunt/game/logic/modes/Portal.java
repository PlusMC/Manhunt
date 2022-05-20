package dev.oakleycord.manhunt.game.logic.modes;

import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.ManHunt;
import dev.oakleycord.manhunt.game.logic.Logic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Portal extends Logic {

    private final PortalListener portalListener;

    public Portal(AbstractRun game) {
        super(game);
        portalListener = new PortalListener();
    }

    @Override
    public void tick(long tick) {

    }

    @Override
    public void load() {
        getGame().getWorldHandler().registerEvents(portalListener);
    }

    @Override
    public void unload() {
        getGame().getWorldHandler().unregisterEvents(portalListener);
    }

    private class PortalListener implements org.bukkit.event.Listener {
        @EventHandler
        public void onPortalTeleport(PlayerTeleportEvent event) {
            if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return;

            if (getGame() instanceof ManHunt manHunt) {
                if (!manHunt.getRunners().hasEntry(event.getPlayer().getName())) return;
                manHunt.win(ManHunt.MHTeam.RUNNERS);
            } else {
                getGame().postGame();
            }
        }
    }
}
