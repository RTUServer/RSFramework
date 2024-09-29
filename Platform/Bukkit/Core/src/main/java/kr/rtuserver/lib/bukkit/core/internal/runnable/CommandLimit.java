package kr.rtuserver.lib.bukkit.core.internal.runnable;

import kr.rtuserver.lib.bukkit.api.RSPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CommandLimit implements kr.rtuserver.lib.bukkit.api.core.internal.runnable.CommandLimit {

    @Getter
    private final Map<UUID, Integer> executeLimit = new ConcurrentHashMap<>();

    public CommandLimit(RSPlugin plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0, 1);
    }

    @Override
    public void run() {
        for (UUID uuid : executeLimit.keySet()) {
            if (executeLimit.get(uuid) > 0) executeLimit.put(uuid, executeLimit.get(uuid) - 1);
            else executeLimit.remove(uuid);
        }
    }
}
