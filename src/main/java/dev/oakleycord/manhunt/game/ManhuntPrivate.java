package dev.oakleycord.manhunt.game;

import org.bukkit.entity.Player;
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


    public Player getHost() {
        if (host.getPlayer() == null)
            return null;
        return ((WrappedPlayerBukkit) host.getPlayer()).getPlayerBukkit();
    }


}
