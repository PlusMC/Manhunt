package dev.oakleycord.manhunt.game;

import dev.oakleycord.manhunt.SpeedRuns;
import org.bukkit.entity.Player;
import org.plusmc.pluslib.bukkit.managing.BaseManager;
import org.plusmc.pluslib.bukkit.managing.PlusItemManager;
import org.plusmc.pluslibcore.mongo.User;
import org.plusmc.pluslibcore.reflection.bungeebukkit.config.InjectableConfig;
import org.plusmc.pluslibcore.reflection.bungeebukkit.player.WrappedPlayerBukkit;

public class ManhuntPrivate extends ManHunt {
    private final User host;

    public ManhuntPrivate(User host, InjectableConfig config) {
        super();
        this.host = host;
        config.inject(this);
    }

    @Override
    public void onPlayerJoin(Player player) {
        super.onPlayerJoin(player);
        if (host.getUUID().equals(player.getUniqueId()))
            giveHostTools(player);
    }

    public void giveHostTools(Player player) {
        //this api sucks TODO: fix up plusitem
        PlusItemManager itemManager = BaseManager.getManager(SpeedRuns.getInstance(), PlusItemManager.class);
        player.getInventory().setItem(3, itemManager.getPlusItem("startGame").getItem());
        player.getInventory().setItem(5, itemManager.getPlusItem("gameSettings").getItem());
    }

    public Player getHost() {
        if (host.getPlayer() == null)
            return null;
        return ((WrappedPlayerBukkit) host.getPlayer()).getPlayerBukkit();
    }


}
