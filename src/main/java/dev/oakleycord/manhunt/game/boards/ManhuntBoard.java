package dev.oakleycord.manhunt.game.boards;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.ManHunt;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.plusmc.pluslib.bukkit.managed.PlusBoard;
import org.plusmc.pluslib.bukkit.managing.BaseManager;
import org.plusmc.pluslib.bukkit.managing.PlusItemManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ManhuntBoard extends PlusBoard {
    private static final Random RANDOM = new Random();
    private final ManHunt game;
    private double flipSpeed = 0;
    private double prevFlipSpeed = 1;
    private Player currentPlayer;

    public ManhuntBoard(ManHunt game) {
        super("§6§l§n§oManHunt");
        this.game = game;
    }

    @Override
    public List<String> getEntries(long tick) {
        List<String> entries = new ArrayList<>();
        entries.add("");
        entries.add("Players: §b%playerAmount%");
        entries.add("Hunters: §b%hunterAmount%");
        entries.add("Runners Alive: §b%runnerAmount%");

        if (game.getState() == GameState.INGAME) {
            entries.add("Time: §b%time%");
        }

        entries.add("Mode: §b%mode%");

        if (!game.getModifiers().isEmpty())
            entries.add("Modifiers: §b%modifiers%");


        entries.add("");
        flipThroughRandomPlayers(tick);
        if (currentPlayer != null)
            entries.add("Host: " + (flipSpeed == -1 ? "§a" : "§c") + currentPlayer.getName());

        entries.add("§8§lGameState: %gameState%");
        return entries;
    }

    @Override
    public boolean useVariables() {
        return true;
    }

    private void flipThroughRandomPlayers(long tick) {
        if (game.getPlayers().size() < 2) return;
        flipSpeed = currentPlayer == null ? 1.0001 : flipSpeed;
        if (flipSpeed == -1) return;
        if (tick % (long) prevFlipSpeed != 0) return;
        prevFlipSpeed = flipSpeed;
        if (tick % (long) flipSpeed == 0) {
            List<Player> players = game.getPlayers();
            players.forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1));
            players.remove(currentPlayer);
            currentPlayer = players.get(RANDOM.nextInt(players.size()));
            flipSpeed = flipSpeed + (RANDOM.nextDouble(0.01, 0.045) * flipSpeed);
            if (flipSpeed > 7.5) {
                flipSpeed = -1;
                currentPlayer.getWorld().playSound(currentPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                PlusItemManager manager = BaseManager.getManager(SpeedRuns.getInstance(), PlusItemManager.class);
                currentPlayer.getInventory().setItem(3, manager.getPlusItem("start_game").getItem());
                currentPlayer.getInventory().setItem(5, manager.getPlusItem("game_settings").getItem());
                currentPlayer.sendMessage("§aYou are the host!");

                Firework firework = currentPlayer.getWorld().spawn(currentPlayer.getLocation(), Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.setPower(0);
                meta.addEffect(FireworkEffect.builder().withColor(Color.LIME).withFade(Color.GREEN).with(FireworkEffect.Type.BALL).build());
                firework.setFireworkMeta(meta);
                firework.detonate();
            }
        }
    }


}
