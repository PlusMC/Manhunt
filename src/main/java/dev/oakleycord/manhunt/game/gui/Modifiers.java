package dev.oakleycord.manhunt.game.gui;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.logic.modifiers.Modifier;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.plusmc.pluslib.gui.GUIElement;
import org.plusmc.pluslib.gui.ItemBuilder;
import org.plusmc.pluslib.managed.PlusGUI;

import java.util.ArrayList;
import java.util.List;

public class Modifiers extends PlusGUI {
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(this, 18, "Modifiers");
    }

    @Override
    public void draw() {
        int i = 1;
        for (Modifier modifier : Modifier.values()) {
            if (ManHunt.GAME == null)
                continue;

            boolean hasModifier = ManHunt.GAME.getModifiers().contains(modifier);

            List<String> lore = new ArrayList<>(List.of(
                    hasModifier ? "§a§lEnabled" : "§c§lDisabled",
                    "§7Click to toggle."
            ));

            lore.addAll(List.of(modifier.description));

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

                ManHunt.GAME.getScoreboardHandler().tick(0);
                draw();
            }), i);
            i += 3;
        }

        ItemStack close = new ItemBuilder(Material.BARRIER).setName("§cClose").build();

        setElement(new GUIElement(close, event -> {
            HumanEntity human = event.getWhoClicked();
            Bukkit.getScheduler().runTask(ManHunt.getInstance(), () -> human.openInventory(new MHSettings().getInventory()));
            if (human instanceof Player p) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1.25f);
        }), 13);
        super.draw();
    }
}
