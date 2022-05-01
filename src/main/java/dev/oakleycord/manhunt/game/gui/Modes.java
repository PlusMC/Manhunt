package dev.oakleycord.manhunt.game.gui;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.logic.modes.Mode;
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
import java.util.Arrays;
import java.util.List;

public class Modes extends PlusGUI {

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(this, 18, "Modes");
    }

    @Override
    public void draw() {
        int i = 1;
        for (Mode mode : Mode.values()) {
            if (ManHunt.GAME == null)
                continue;

            boolean isGameMode = ManHunt.GAME.getGameMode() == mode;

            List<String> lore = new ArrayList<>(List.of("", "§7Click to select this mode."));
            if (isGameMode)
                lore.set(0, "§aCurrent Game Mode.");
            else lore.remove(0);
            lore.addAll(Arrays.asList(mode.description));

            ItemStack item = new ItemBuilder(mode.icon).setName(mode.name() + "%").addEnchant(OtherUtil.EMPTY_ENCHANT, 0).setLore(lore).build();


            setElement(new GUIElement(item, (event) -> {
                if (ManHunt.GAME == null)
                    return;

                boolean isGameMode2 = ManHunt.GAME.getGameMode() == mode;

                if (!isGameMode2)
                    ManHunt.GAME.setGameMode(mode);

                if (event.getWhoClicked() instanceof Player p)
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 1, isGameMode2 ? 0.75f : 1.25f);

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
