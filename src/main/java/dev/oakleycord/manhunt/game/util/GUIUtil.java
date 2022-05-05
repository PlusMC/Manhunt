package dev.oakleycord.manhunt.game.util;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.gui.MHSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.plusmc.pluslib.bukkit.gui.GUIElement;
import org.plusmc.pluslib.bukkit.gui.ItemBuilder;
import org.plusmc.pluslib.bukkit.managed.PaginatedGUI;
import org.plusmc.pluslib.bukkit.managed.PlusGUI;

import java.util.Map;

public class GUIUtil {
    private GUIUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void addCloseElement(PlusGUI gui) {
        ItemStack close = new ItemBuilder(Material.BARRIER).setName("§cClose").build();

        gui.setElement(new GUIElement(close, event -> {
            HumanEntity human = event.getWhoClicked();
            Bukkit.getScheduler().runTask(ManHunt.getInstance(), () -> human.openInventory(new MHSettings().getInventory()));
            if (human instanceof Player p) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1.25f);
        }), gui.getInventory().getSize() - 5);
    }

    public static void addCloseElement(Map<Integer, GUIElement> gui, int size) {
        ItemStack close = new ItemBuilder(Material.BARRIER).setName("§cClose").build();

        gui.put(size - 5, new GUIElement(close, event -> {
            HumanEntity human = event.getWhoClicked();
            Bukkit.getScheduler().runTask(ManHunt.getInstance(), () -> human.openInventory(new MHSettings().getInventory()));
            if (human instanceof Player p) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1.25f);
        }));
    }

    public static GUIElement getBackPageElement(PaginatedGUI gui) {
        ItemStack back = new ItemBuilder(Material.ARROW).setName("§cBack").build();
        return new GUIElement(back, event -> {
            if (gui.getPage() - 1 < 0) return;
            gui.setPage(gui.getPage() - 1, true);
            if (event.getWhoClicked() instanceof Player p) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 0.75f);
            }
        });
    }

    public static GUIElement getNextPageElement(PaginatedGUI gui) {
        ItemStack next = new ItemBuilder(Material.ARROW).setName("§cNext").build();
        return new GUIElement(next, event -> {
            if (gui.getPage() + 1 > gui.getPageAmount()) return;
            gui.setPage(gui.getPage() + 1, true);
            if (event.getWhoClicked() instanceof Player p)
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1.25f);
        });
    }
}
