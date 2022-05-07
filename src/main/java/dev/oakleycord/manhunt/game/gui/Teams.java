package dev.oakleycord.manhunt.game.gui;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.GameTeam;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.util.GUIUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.plusmc.pluslib.bukkit.gui.GUIElement;
import org.plusmc.pluslib.bukkit.gui.ItemBuilder;
import org.plusmc.pluslib.bukkit.managed.PaginatedGUI;

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
        if (!ManHunt.hasGame()) return;
        MHGame game = ManHunt.getGame();

        for (int i = 0; i < (game.getPlayers().size() / 27) + 1; i++) {
            Map<Integer, GUIElement> page = new HashMap<>();
            int index = 0;
            for (int j = 0; j < 27 && index < game.getPlayers().size(); j++, index = i * 27 + j) {
                System.out.println(index);
                Player player = game.getPlayers().get(index);
                if (!game.hasTeam(player)) continue;

                page.put(j, getHeadElement(player, game));
            }
            ItemStack empty = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build();
            for (int j = 27; j < 36; j++) page.put(j, new GUIElement(empty, null));


            page.put(30, GUIUtil.getBackPageElement(this));
            GUIUtil.addCloseElement(page, this.getInventory().getSize());
            page.put(32, GUIUtil.getNextPageElement(this));

            addPage(i, page);
        }
    }

    private GUIElement getHeadElement(Player player, MHGame game) {
        String team = game.getTeam(player).getPrefix();
        ItemStack head = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(player).setLore(
                "§fTeam: " + team,
                "§7Left Click to change to Runner",
                "§7Right Click to change to Hunter",
                "§7Q to change to Spectator"
        ).build();

        return new GUIElement(head, event -> {
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
                default -> {
                    if (!(event.getWhoClicked() instanceof Player p)) return;
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 1f, 0.75f);
                }
            }

            game.getScoreboardHandler().tick(0);

            ItemMeta meta = head.getItemMeta();
            assert meta != null;
            List<String> lore = meta.getLore();
            assert lore != null;
            lore.set(0, "§fTeam: " + game.getTeam(player).getPrefix());
            meta.setLore(lore);
            head.setItemMeta(meta);
            draw();
        });
    }

}
