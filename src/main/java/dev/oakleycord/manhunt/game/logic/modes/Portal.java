package dev.oakleycord.manhunt.game.logic.modes;

import dev.oakleycord.manhunt.game.GameTeam;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Portal extends Logic {

    private final PortalListener portalListener;

    public Portal(MHGame game) {
        super(game);
        portalListener = new PortalListener();
    }

    @Override
    public void tick(long tick) {
        MHGame game = getGame();

        if (game.getRunners().getSize() == 0)
            game.postGame(GameTeam.HUNTERS);
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
            MHGame game = getGame();
            if (!game.getRunners().hasEntry(event.getPlayer().getName())) return;
            game.postGame(GameTeam.RUNNERS);
        }
    }
}
