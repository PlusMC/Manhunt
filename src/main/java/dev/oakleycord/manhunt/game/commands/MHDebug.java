package dev.oakleycord.manhunt.game.commands;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.gui.MHSettings;
import dev.oakleycord.manhunt.game.util.ParticleUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.plusmc.pluslib.bukkit.managed.PlusCommand;

import java.util.List;


//i have no idea why i made this class maybe i can rename it and change it into an "admin" command
public class MHDebug implements PlusCommand {

    @Override
    public String getName() {
        return "MHDebug";
    }

    @Override
    public String getPermission() {
        return "manhunt.debug";
    }

    @Override
    public String getUsage() {
        return "MHDebug <option>";
    }

    @Override
    public String getDescription() {
        return "Helpful for debugging idk lol";
    }

    @Override
    public List<String> getCompletions(int index) {
        return List.of("tpWorld", "testParticle", "openInv");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return false;
        if (!(sender instanceof Player p)) return false;

        switch (args[0]) {
            case "tpWorld" -> p.teleport(ManHunt.getGame().getOverworld().getSpawnLocation());
            case "testParticle" -> {
                p.getLocation().add(4, 4, 4).getBlock().setType(Material.BEDROCK);
                for (int i = 0; i < 200; i++) {
                    ParticleUtil.helixTicked(p, Color.fromRGB(15, 200, 15), i);
                }
            }
            case "openInv" -> p.openInventory(new MHSettings().getInventory());
        }
        return true;
    }
}
