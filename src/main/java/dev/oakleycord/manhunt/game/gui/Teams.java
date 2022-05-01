package dev.oakleycord.manhunt.game.gui;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.GameTeam;
import dev.oakleycord.manhunt.game.MHGame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.plusmc.pluslib.bukkit.gui.GUIElement;
import org.plusmc.pluslib.bukkit.gui.ItemBuilder;
import org.plusmc.pluslib.bukkit.managed.PaginatedGUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teams extends PaginatedGUI {

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(this, 36, "Teams");
    }

    @Override
    protected void createPages() {
        if (ManHunt.GAME == null) return;
        MHGame game = ManHunt.GAME;
        List<Map<Integer, GUIElement>> pages = new ArrayList<>();
        for (int i = 0; i < (game.getPlayers().size() / 27) + 1; i++) {
            Map<Integer, GUIElement> page = new HashMap<>();
            for (int j = 0; j < 27; j++) {
                int index = i * 27 + j;
                if (index >= game.getPlayers().size()) break;
                Player player = game.getPlayers().get(index);
                if (!game.hasTeam(player)) continue;
                String team = game.getTeam(player).getPrefix();
                ItemStack head = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(player).setLore(
                        "§fTeam: " + team,
                        "§7Left Click to change to Runner",
                        "§7Right Click to change to Hunter",
                        "§7Q to change to Spectator"
                ).build();

                page.put(j, new GUIElement(head, event -> {
                    switch (event.getClick()) {
                        case LEFT -> {
                            game.setTeam(player, GameTeam.RUNNERS);
                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 2);
                            player.sendMessage("§aYou are now a Runner!");
                        }
                        case RIGHT -> {
                            game.setTeam(player, GameTeam.HUNTERS);
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PILLAGER_AMBIENT, 1, 1);
                            player.sendMessage("§aYou are now a Hunter!");
                        }
                        case DROP -> {
                            game.setTeam(player, GameTeam.SPECTATORS);
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_AMBIENT, 1, 1);
                            player.sendMessage("§aYou are now a Spectator!");
                        }
                    }

                    ManHunt.GAME.getScoreboardHandler().tick(0);

                    ItemMeta meta = head.getItemMeta();
                    assert meta != null;
                    List<String> lore = meta.getLore();
                    assert lore != null;
                    lore.set(0, "§fTeam: " + game.getTeam(player).getPrefix());
                    meta.setLore(lore);
                    head.setItemMeta(meta);
                    draw();
                }));
            }
            ItemStack empty = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build();
            for (int j = 27; j < 36; j++) page.put(j, new GUIElement(empty, null));

            ItemStack back = new ItemBuilder(Material.ARROW).setName("§cBack").build();
            ItemStack next = new ItemBuilder(Material.ARROW).setName("§cNext").build();
            ItemStack close = new ItemBuilder(Material.BARRIER).setName("§cClose").build();


            page.put(30, new GUIElement(back, event -> {
                if (getPage() - 1 < 0) return;
                setPage(getPage() - 1, true);
                if (event.getWhoClicked() instanceof Player p) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 0.75f);
                }
            }));

            page.put(31, new GUIElement(close, event -> {
                HumanEntity human = event.getWhoClicked();
                Bukkit.getScheduler().runTask(ManHunt.getInstance(), () -> human.openInventory(new MHSettings().getInventory()));
                if (human instanceof Player p) p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 1.25f);
            }));

            page.put(32, new GUIElement(next, event -> {
                if (getPage() + 1 > getPageAmount()) return;
                setPage(getPage() + 1, true);
                if (event.getWhoClicked() instanceof Player p)
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1.25f);
            }));


            pages.add(page);
        }

        for (int i = 0; i < pages.size(); i++)
            addPage(i, pages.get(i));

    }

}
