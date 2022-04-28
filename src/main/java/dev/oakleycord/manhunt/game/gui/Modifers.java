package dev.oakleycord.manhunt.game.gui;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.logic.modifiers.Modifier;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.plusmc.pluslib.gui.GUIElement;
import org.plusmc.pluslib.gui.ItemBuilder;
import org.plusmc.pluslib.managed.PlusGUI;

import java.util.Arrays;
import java.util.List;

public class Modifers extends PlusGUI {
    @Override
    protected Inventory createInventory() {
        int i = 1;
        for (Modifier modifier : Modifier.values()) {
            if (ManHunt.GAME == null)
                continue;

            boolean hasModifier = ManHunt.GAME.getModifiers().contains(modifier);

            String[] lore = new String[]{
                    hasModifier ? "§a§lEnabled" : "§c§lDisabled",
                    "§7Click to toggle."
            };
            String[] description = modifier.description;
            if (description.length > 0)
                lore = Arrays.copyOf(lore, lore.length + description.length);
            System.arraycopy(description, 0, lore, lore.length - description.length, description.length);

            ItemStack item = new ItemBuilder(modifier.icon).setName(modifier.name).addEnchant(OtherUtil.EMPTY_ENCHANT, 0).setLore(lore).build();

            setElement(new GUIElement(item, (event) -> {
                if (ManHunt.GAME == null)
                    return;

                boolean hasMod = ManHunt.GAME.getModifiers().contains(modifier);

                if (hasMod)
                    ManHunt.GAME.removeModifier(modifier);
                else ManHunt.GAME.addModifier(modifier);

                if (event.getWhoClicked() instanceof Player p)
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 1, hasMod ? 0.75f : 1.25f);

                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                List<String> loreList = meta.getLore();
                assert loreList != null;
                loreList.set(0, hasMod ? "§c§lDisabled" : "§a§lEnabled");
                meta.setLore(loreList);
                item.setItemMeta(meta);

                ManHunt.GAME.getScoreboardHandler().tick(0);

                draw();
            }), i);
            i += 3;
        }


        return Bukkit.createInventory(this, 9, "Modifiers");
    }
}
