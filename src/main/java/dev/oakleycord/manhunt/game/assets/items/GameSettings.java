package dev.oakleycord.manhunt.game.assets.items;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.assets.gui.MHSettings;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.plusmc.pluslib.bukkit.managed.PlusItem;

import static net.md_5.bungee.api.ChatColor.AQUA;
import static net.md_5.bungee.api.ChatColor.BOLD;

public class GameSettings implements PlusItem {
    @Override
    public Material getMaterial() {
        return Material.COMPARATOR;
    }

    @Override
    public String getName() {
        return AQUA + "" + BOLD + "Game Settings";
    }

    @Override
    public String[] getLore() {
        return new String[0];
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        event.getPlayer().openInventory(new MHSettings(SpeedRuns.getGame()).getInventory());
        event.setCancelled(true);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @Override
    public String getID() {
        return "gameSettings";
    }
}
