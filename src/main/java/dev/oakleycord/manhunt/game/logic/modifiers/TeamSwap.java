package dev.oakleycord.manhunt.game.logic.modifiers;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class TeamSwap extends Logic {

    private long triggerTime = 0;

    public TeamSwap(MHGame game) {
        super(game);
    }

    @Override
    public void update(long tick) {
        MHGame game = getGame();

        if (triggerTime == 0) {
            long delay = (long) ((Math.random() + 0.3) * 120) * 20;
            triggerTime = tick + delay;
            game.getPlayers().forEach(p -> {
                p.sendMessage("§6The teams will swap in §e" + (delay / 20) + " §6seconds!");
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            });
        }
        long seconds = (triggerTime - tick) / 20;

        if (seconds < 10 && tick % 20 == 0) {
            game.getPlayers().forEach(p -> {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6Teams swapping in §e" + seconds + " §6seconds!"));
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
            });
        }

        if (tick < triggerTime) return;

        Set<String> hunters = game.getHunters().getEntries();
        hunters.forEach(e -> {
            game.getHunters().removeEntry(e);
            game.getRunners().addEntry(e);
        });

        game.getRunners().getEntries().forEach(e -> {
            if (!hunters.contains(e)) {
                game.getRunners().removeEntry(e);
                game.getHunters().addEntry(e);
                Player p = Bukkit.getPlayer(e);
                if (p != null)
                    if (!p.getInventory().contains(Material.COMPASS))
                        p.getInventory().addItem(new ItemStack(Material.COMPASS));
            }
        });

        game.getPlayers().forEach(p -> {
            p.sendMessage("§6The teams have swapped!");
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1, 0.5f);
        });

        triggerTime = 0;
    }
}
