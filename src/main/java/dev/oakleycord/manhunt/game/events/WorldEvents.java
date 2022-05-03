package dev.oakleycord.manhunt.game.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldEvents implements Listener {
    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        event.getWorld().setKeepSpawnInMemory(false);
    }
}
