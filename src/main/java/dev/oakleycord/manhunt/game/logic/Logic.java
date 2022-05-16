package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.ManHunt;
import dev.oakleycord.manhunt.game.MHGame;
import org.plusmc.pluslib.bukkit.managed.Tickable;
import org.plusmc.pluslib.reflection.timings.ITimings;

public abstract class Logic implements Tickable {
    private final MHGame game;
    private final ITimings timings;

    protected Logic(MHGame game) {
        this.game = game;
        this.timings = ITimings.create(ManHunt.getInstance(), this.getClass().getSimpleName());
    }

    public ITimings getTimings() {
        return this.timings;
    }

    public MHGame getGame() {
        return game; //hi there :)
    }
}
