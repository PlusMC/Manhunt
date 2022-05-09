package dev.oakleycord.manhunt.game.items;

import dev.oakleycord.manhunt.game.gui.MHSettings;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.plusmc.pluslib.bukkit.managed.PlusItem;

public class GameSettingsItem implements PlusItem {
    @Override
    public String getID() {
        return "game_settings";
    }

    @Override
    public String getName() {
        return "§6Game Settings";
    }

    @Override
    public String[] getLore() {
        return new String[]{
                "§bOpens the Game Settings menu"
        };
    }

    @Override
    public Material getMaterial() {
        return Material.PISTON;
    }

    @Override
    public void onInteractBlock(PlayerInteractEvent event) {
        event.getPlayer().openInventory(new MHSettings().getInventory());
    }
}
