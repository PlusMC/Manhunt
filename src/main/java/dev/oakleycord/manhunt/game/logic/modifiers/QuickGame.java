package dev.oakleycord.manhunt.game.logic.modifiers;

import dev.oakleycord.manhunt.game.MHGame;
import dev.oakleycord.manhunt.game.logic.Logic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class QuickGame extends Logic {

    protected QuickGame(MHGame game) {
        super(game);
    }

    public static ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_HELMET)
        };
    }

    public static ItemStack[] getItems() {
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

    @Override
    public void tick(long tick) {
    }

    public void load() {
        getGame().getPlayers().forEach(player -> {
            player.getInventory().setContents(getItems());
            player.getInventory().setArmorContents(getArmor());
            player.getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));
        });
    }
}
