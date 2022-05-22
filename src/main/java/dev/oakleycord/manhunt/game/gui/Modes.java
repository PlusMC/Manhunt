package dev.oakleycord.manhunt.game.gui;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
import dev.oakleycord.manhunt.game.logic.modes.Mode;
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
import java.util.Arrays;
import java.util.List;

public class Modes extends PlusGUI {

    private final AbstractRun game;

    public Modes(AbstractRun game) {
        super(false);
        this.game = game;
        regenerateInventory();
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(this, 18, "Modes");
    }

    @Override
    public void draw() {
        int i = 1;
        for (Mode mode : Mode.values()) {
            setElement(getModeElement(mode, game), i);
            i += 3;
        }

        GUIUtil.addCloseElement(this, game);
        super.draw();
    }


    private GUIElement getModeElement(Mode mode, AbstractRun game) {
        boolean isGameMode = game.getGameMode() == mode;

        List<String> lore = new ArrayList<>(List.of("", "§7Click to select this mode."));
        if (isGameMode)
            lore.set(0, "§aCurrent Game Mode.");
        else lore.remove(0);
        lore.addAll(Arrays.asList(mode.getDescription()));

        ItemStack item = new ItemBuilder(mode.icon).setName(mode.name() + "%").addEnchant(OtherUtil.EMPTY_ENCHANT, 0).setLore(lore).build();

        return new GUIElement(item, event -> {
            if (!SpeedRuns.hasGame())
                return;

            boolean isGameMode2 = game.getGameMode() == mode;

            if (!isGameMode2)
                game.setGameMode(mode);

            if (event.getWhoClicked() instanceof Player p)
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 1, isGameMode2 ? 0.75f : 1.25f);

            game.getPlusBoard().tick(0);

            draw();
        });
    }
}
