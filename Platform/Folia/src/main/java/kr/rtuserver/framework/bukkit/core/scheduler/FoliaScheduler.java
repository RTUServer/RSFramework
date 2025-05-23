package kr.rtuserver.framework.bukkit.core.scheduler;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.core.scheduler.ScheduledTask;
import kr.rtuserver.framework.bukkit.api.core.scheduler.Scheduler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class FoliaScheduler implements Scheduler {

    private final GlobalRegionScheduler global = Bukkit.getGlobalRegionScheduler();
    private final RegionScheduler region = Bukkit.getRegionScheduler();
    private final AsyncScheduler async = Bukkit.getAsyncScheduler();

    private boolean isValid(Location location) {
        if (location == null) return false;
        return location.getWorld() != null;
    }

    private boolean isValid(Entity entity) {
        if (entity == null) return false;
        if (entity.isValid()) {
            if (entity instanceof Player player) return player.isOnline();
            if (entity instanceof Projectile projectile) return !projectile.isDead();
        }
        return false;
    }

    @Override
    public void run(RSPlugin plugin, Consumer<ScheduledTask> consumer) {
        global.run(plugin, task -> consumer.accept(new FoliaTask(task)));
    }

    @Override
    public ScheduledTask run(RSPlugin plugin, Runnable runnable) {
        return new FoliaTask(global.run(plugin, scheduledTask -> runnable.run()));
    }

    @Override
    public void runLater(RSPlugin plugin, Consumer<ScheduledTask> consumer, long delay) {
        global.runDelayed(plugin, task -> consumer.accept(new FoliaTask(task)), delay);
    }

    @Override
    public ScheduledTask runLater(RSPlugin plugin, Runnable runnable, long delay) {
        return new FoliaTask(global.runDelayed(plugin, scheduledTask -> runnable.run(), delay));
    }

    @Override
    public void runTimer(RSPlugin plugin, Consumer<ScheduledTask> consumer, long delay, long period) {
        global.runAtFixedRate(plugin, task -> consumer.accept(new FoliaTask(task)), delay, period);
    }

    @Override
    public ScheduledTask runTimer(RSPlugin plugin, Runnable runnable, long delay, long period) {
        return new FoliaTask(global.runAtFixedRate(plugin, scheduledTask -> runnable.run(), delay, period));
    }

    @Override
    public void runAsync(RSPlugin plugin, Consumer<ScheduledTask> consumer) {
        async.runNow(plugin, task -> consumer.accept(new FoliaTask(task)));
    }

    @Override
    public ScheduledTask runAsync(RSPlugin plugin, Runnable runnable) {
        return new FoliaTask(async.runNow(plugin, scheduledTask -> runnable.run()));
    }

    @Override
    public void runLaterAsync(RSPlugin plugin, Consumer<ScheduledTask> consumer, long delay) {
        async.runDelayed(plugin, task -> consumer.accept(new FoliaTask(task)), delay * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledTask runLaterAsync(RSPlugin plugin, Runnable runnable, long delay) {
        return new FoliaTask(async.runDelayed(plugin, scheduledTask -> runnable.run(), delay * 50, TimeUnit.MILLISECONDS));
    }

    @Override
    public void runTimerAsync(RSPlugin plugin, Consumer<ScheduledTask> consumer, long delay, long period) {
        async.runAtFixedRate(plugin, task -> consumer.accept(new FoliaTask(task)), delay * 50, period * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public ScheduledTask runTimerAsync(RSPlugin plugin, Runnable runnable, long delay, long period) {
        return new FoliaTask(async.runAtFixedRate(plugin, scheduledTask -> runnable.run(), delay * 50, period * 50, TimeUnit.MILLISECONDS));
    }

    @Override
    public void run(RSPlugin plugin, Location location, Consumer<ScheduledTask> consumer) {
        if (isValid(location)) region.run(plugin, location, task -> consumer.accept(new FoliaTask(task)));
    }

    @Override
    public ScheduledTask run(RSPlugin plugin, Location location, Runnable runnable) {
        if (isValid(location)) return run(plugin, runnable);
        return null;
    }

    @Override
    public void runLater(RSPlugin plugin, Location location, Consumer<ScheduledTask> consumer, long delay) {
        if (isValid(location)) region.runDelayed(plugin, location, task -> consumer.accept(new FoliaTask(task)), delay);
    }

    @Override
    public ScheduledTask runLater(RSPlugin plugin, Location location, Runnable runnable, long delay) {
        if (isValid(location)) return runLater(plugin, runnable, delay);
        return null;
    }

    @Override
    public void runTimer(RSPlugin plugin, Location location, Consumer<ScheduledTask> consumer, long delay, long period) {
        if (isValid(location))
            region.runAtFixedRate(plugin, location, task -> consumer.accept(new FoliaTask(task)), delay, period);
    }

    @Override
    public ScheduledTask runTimer(RSPlugin plugin, Location location, Runnable runnable, long delay, long period) {
        if (isValid(location)) return runTimer(plugin, runnable, delay, period);
        return null;
    }

    @Override
    public void run(RSPlugin plugin, Entity entity, Consumer<ScheduledTask> consumer) {
        if (isValid(entity)) entity.getScheduler().run(plugin, task -> consumer.accept(new FoliaTask(task)), null);
    }

    @Override
    public ScheduledTask run(RSPlugin plugin, Entity entity, Runnable runnable) {
        if (isValid(entity)) return run(plugin, runnable);
        return null;
    }

    @Override
    public void runLater(RSPlugin plugin, Entity entity, Consumer<ScheduledTask> consumer, long delay) {
        if (isValid(entity))
            entity.getScheduler().runDelayed(plugin, task -> consumer.accept(new FoliaTask(task)), null, delay);
    }

    @Override
    public ScheduledTask runLater(RSPlugin plugin, Entity entity, Runnable runnable, long delay) {
        if (isValid(entity)) return runLater(plugin, runnable, delay);
        return null;
    }

    @Override
    public void runTimer(RSPlugin plugin, Entity entity, Consumer<ScheduledTask> consumer, long delay, long period) {
        if (isValid(entity))
            entity.getScheduler().runAtFixedRate(plugin, task -> consumer.accept(new FoliaTask(task)), null, delay, period);
    }

    @Override
    public ScheduledTask runTimer(RSPlugin plugin, Entity entity, Runnable runnable, long delay, long period) {
        if (isValid(entity)) return runTimer(plugin, runnable, delay, period);
        return null;
    }

}
