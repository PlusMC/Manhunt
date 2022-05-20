package dev.oakleycord.manhunt.game.gui;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.ManHunt;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.plusmc.pluslib.bukkit.gui.GUIElement;
import org.plusmc.pluslib.bukkit.gui.ItemBuilder;
import org.plusmc.pluslib.bukkit.managed.PlusGUI;

public class MHSettings extends PlusGUI {

    private final AbstractRun game;

    public MHSettings(AbstractRun game) {
        super();
        this.game = game;
    }

    @Override
    protected Inventory createInventory() {
        ItemStack teams = new ItemBuilder(Material.DIAMOND_SWORD).setName("§6Teams").addEnchant(OtherUtil.EMPTY_ENCHANT, 0).setLore("Set Player Teams!").build();
        ItemStack mode = new ItemBuilder(Material.END_PORTAL_FRAME).setName("§6Modes").addEnchant(OtherUtil.EMPTY_ENCHANT, 0).setLore("Set Game Mode!").build();
        ItemStack modifiers = new ItemBuilder(Material.POTION).setName("§6Modifiers").addEnchant(OtherUtil.EMPTY_ENCHANT, 0).setLore("Set Game Modifiers!").build();
        PotionMeta meta = (PotionMeta) modifiers.getItemMeta();
        assert meta != null;
        meta.setColor(Color.RED);
        modifiers.setItemMeta(meta);

        if (game instanceof ManHunt manHunt) {
            setElement(new GUIElement(teams, event -> {
                if (event.getWhoClicked() instanceof Player p)
                    p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1, 1);
                Bukkit.getScheduler().runTask(SpeedRuns.getInstance(), () -> event.getWhoClicked().openInventory(new Teams(manHunt).getInventory()));
            }), 1);
        }

        setElement(new GUIElement(mode, event -> {
            if (event.getWhoClicked() instanceof Player p)
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1, 0.1f);

            Bukkit.getScheduler().runTask(SpeedRuns.getInstance(), () -> event.getWhoClicked().openInventory(new Modes(game).getInventory()));
        }), 4);


        setElement(new GUIElement(modifiers, event -> {
            if (event.getWhoClicked() instanceof Player p)
                p.playSound(p.getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_MILK, 1, 1);

            Bukkit.getScheduler().runTask(SpeedRuns.getInstance(), () -> event.getWhoClicked().openInventory(new Modifiers(game).getInventory()));
        }), 7);

        return Bukkit.createInventory(this, 9, "Manhunt Settings");
    }
}
