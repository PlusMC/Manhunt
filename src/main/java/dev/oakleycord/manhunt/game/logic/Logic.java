package dev.oakleycord.manhunt.game.logic;

import dev.oakleycord.manhunt.SpeedRuns;
import dev.oakleycord.manhunt.game.AbstractRun;
import org.plusmc.pluslib.bukkit.managed.Tickable;
import org.plusmc.pluslibcore.reflect.spigotpaper.timings.ITimings;

public abstract class Logic implements Tickable {
    private final AbstractRun game;
    private final ITimings timings;

    protected Logic(AbstractRun game) {
        this.game = game;
        this.timings = ITimings.create(SpeedRuns.getInstance(), this.getClass().getSimpleName());
    }

    public ITimings getTimings() {
        return this.timings;
    }

    public AbstractRun getGame() {
        return game; //hi there :)
    }
}
