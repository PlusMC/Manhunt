package dev.oakleycord.manhunt.game.logic.modifiers;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.GameState;
import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class QuickGame extends Logic {

    private final DeathListener deathListener;

    protected QuickGame(MHGame game) {
        super(game);
        deathListener = new DeathListener();
    }

    @Override
    public void tick(long tick) {
        //this modifier has nothing to tick
    }

    @Override
    public void load() {
        getGame().getPlayers().forEach(this::giveItems);
        Bukkit.getPluginManager().registerEvents(deathListener, ManHunt.getInstance());
    }

    private void giveItems(Player player) {
        player.getInventory().setContents(getItems());
        player.getInventory().setArmorContents(getArmor());
        player.getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));
    }

    private ItemStack[] getItems() {
        ItemStack[] items = new ItemStack[9];
        items[0] = new ItemStack(Material.IRON_SWORD);
        items[1] = new ItemStack(Material.IRON_PICKAXE);
        items[2] = new ItemStack(Material.IRON_AXE);
        items[3] = new ItemStack(org.bukkit.Material.BOW);
        items[4] = new ItemStack(org.bukkit.Material.ARROW);
        items[5] = new ItemStack(Material.COOKED_BEEF);
        items[6] = new ItemStack(Material.OAK_WOOD);
        items[7] = new ItemStack(Material.FLINT_AND_STEEL);
        items[8] = new ItemStack(Material.WATER_BUCKET);


        items[4].setAmount(64);
        items[5].setAmount(64);
        items[6].setAmount(64);
        return items;
    }

    private ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_HELMET)
        };
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(deathListener);
        if (getGame().getState().equals(GameState.PREGAME))
            getGame().getPlayers().stream().map(Player::getInventory).forEach(Inventory::clear);
    }

    private class DeathListener implements Listener {

        //low priority so we can give the player the items after resetting their inventory
        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
        public void onEntityDamage(EntityDamageEvent event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (!getGame().getPlayers().contains(player)) return;
            if (player.getHealth() - event.getFinalDamage() <= 0)
                giveItems(player);
        }
    }
}
