package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
import org.plusmc.pluslib.bukkit.managed.Tickable;
import org.plusmc.pluslibcore.reflection.bukkitpaper.timings.WrappedTimings;

public abstract class Logic implements Tickable {
    private final AbstractRun game;
    private final WrappedTimings timings;

    protected Logic(AbstractRun game) {
        this.game = game;
        this.timings = WrappedTimings.create(SpeedRuns.getInstance(), this.getClass().getSimpleName());
    }

    public WrappedTimings getTimings() {
        return this.timings;
    }

    public AbstractRun getGame() {
        return game; //hi there :)
    }
}
