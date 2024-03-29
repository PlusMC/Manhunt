package dev.oakleycord.manhunt.game.assets.gui;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.logic.modifiers.Modifier;
import dev.oakleycord.manhunt.game.util.GUIUtil;
import dev.oakleycord.manhunt.game.util.OtherUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.plusmc.pluslib.bukkit.gui.GUIElement;
import org.plusmc.pluslib.bukkit.gui.ItemBuilder;
import org.plusmc.pluslib.bukkit.managed.PlusGUI;

import java.util.ArrayList;
import java.util.List;

public class Modifiers extends PlusGUI {
    private final AbstractRun game;

    public Modifiers(AbstractRun game) {
        super(false);
        this.game = game;
        regenerateInventory();
    }


    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(this, 27, "Modifiers");
    }

    @Override
    public void draw() {
        int i = 1;
        for (Modifier modifier : Modifier.values()) {

            setElement(getModifierElement(modifier, game), i);
            i += 3;
        }

        GUIUtil.addCloseElement(this, game);
        super.draw();
    }

    private GUIElement getModifierElement(Modifier modifier, AbstractRun game) {
        boolean hasModifier = game.getModifiers().contains(modifier);

        List<String> lore = new ArrayList<>(List.of(
                hasModifier ? "§a§lEnabled" : "§c§lDisabled",
                "§7Click to toggle."
        ));

        lore.addAll(List.of(modifier.getDescription()));

        ItemStack item = new ItemBuilder(modifier.icon).setName(modifier.itemName).addEnchant(OtherUtil.EMPTY_ENCHANT, 0).setLore(lore).build();

        return new GUIElement(item, event -> {
            if (!SpeedRuns.hasGame())
                return;

            boolean hasMod = game.getModifiers().contains(modifier);

            if (hasMod)
                game.removeModifier(modifier);
            else game.addModifier(modifier);

            if (event.getWhoClicked() instanceof Player p)
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 1, hasMod ? 0.75f : 1.25f);

            game.getPlusBoard().tick(0);
            draw();
        });
    }
}
