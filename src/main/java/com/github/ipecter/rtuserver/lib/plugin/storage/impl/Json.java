package com.github.ipecter.rtuserver.lib.plugin.storage.impl;

import com.github.ipecter.rtuserver.lib.plugin.RSPlugin;
import com.github.ipecter.rtuserver.lib.plugin.storage.Storage;
import com.github.ipecter.rtuserver.lib.plugin.storage.config.JsonConfig;
import com.github.ipecter.rtuserver.lib.util.common.ComponentUtil;
import com.google.common.io.Files;
import com.google.gson.*;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Json implements Storage {

    private final RSPlugin plugin;

    private final Map<String, JsonFile> map = new HashMap<>();

    public Json(RSPlugin plugin, File[] files) {
        this.plugin = plugin;
        JsonConfig config = plugin.getConfigurations().getJson();
        for (File file : files) {
            try {
                String name = Files.getNameWithoutExtension(file.getName());
                JsonElement json = JsonParser.parseReader(new FileReader(file));
                map.put(name, new JsonFile(plugin, file, json != null && !json.isJsonNull() ? json.getAsJsonArray() : new JsonArray(),
                        config.getSavePeriod()));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean add(String name, JsonObject data) {
        if (!map.containsKey(name)) {
            plugin.console(ComponentUtil.miniMessage("<red>Can't load " + name + " data!</red>"));
            plugin.console(ComponentUtil.miniMessage("<red>" + name + " 파일을 불러오는 도중 오류가 발생하였습니다!</red>"));
            return false;
        }
        return map.get(name).add(data);
    }

    @Override
    public boolean set(String name, Pair<String, Object> find, Pair<String, Object> data) {
        if (!map.containsKey(name)) {
            plugin.console(ComponentUtil.miniMessage("<red>Can't load " + name + " data!</red>"));
            plugin.console(ComponentUtil.miniMessage("<red>" + name + " 파일을 불러오는 도중 오류가 발생하였습니다!</red>"));
            return false;
        }
        return map.get(name).set(find, data);
    }

    @Override
    public List<JsonObject> get(String name, Pair<String, Object> find) {
        if (!map.containsKey(name)) {
            plugin.console(ComponentUtil.miniMessage("<red>Can't load " + name + " data!</red>"));
            plugin.console(ComponentUtil.miniMessage("<red>" + name + " 파일을 불러오는 도중 오류가 발생하였습니다!</red>"));
            return null;
        }
        return map.get(name).get(find);
    }

    public boolean sync(String name) {
        if (!map.containsKey(name)) {
            plugin.console(ComponentUtil.miniMessage("<red>Can't load " + name + " data!</red>"));
            plugin.console(ComponentUtil.miniMessage("<red>" + name + " 파일을 불러오는 도중 오류가 발생하였습니다!</red>"));
            return false;
        }
        return map.get(name).sync();
    }

    public void close() {
        for (JsonFile data : map.values()) data.close();
        map.clear();
    }

    private static class JsonFile {

        private final RSPlugin plugin;

        private final Gson gson = new Gson();
        private final File file;
        @Getter
        private final AtomicBoolean needSave = new AtomicBoolean(false);
        private final BukkitTask task;
        @Getter
        private JsonArray data;

        protected JsonFile(RSPlugin plugin, File file, JsonArray data, int savePeriod) {
            this.plugin = plugin;
            this.file = file;
            this.data = data;
            this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                if (!needSave.get()) return;
                Bukkit.getScheduler().runTask(plugin, () -> {
                    save();
                    needSave.set(false);
                });
            }, savePeriod, savePeriod);
        }

        public boolean add(JsonObject value) {
            data.add(value);
            needSave.lazySet(true);
            return true;
        }

        protected boolean set(Pair<String, Object> find, Pair<String, Object> value) {
            Map<Integer, JsonObject> list = find(find);
            if (list.isEmpty()) return false;
            final JsonArray backup = data;
            List<Integer> toRemove = new ArrayList<>();
            for (int key : list.keySet()) {
                JsonElement resultValue = list.get(key);
                if (resultValue == null || resultValue.isJsonNull()) return false;
                JsonObject valObj = resultValue.getAsJsonObject();
                if (value == null) {
                    toRemove.add(key);
                } else {
                    Object object = value.getValue();
                    if (object instanceof JsonElement element) {
                        valObj.add(value.getKey(), element);
                    } else if (object instanceof Number number) {
                        valObj.addProperty(value.getKey(), number);
                    } else if (object instanceof Boolean bool) {
                        valObj.addProperty(value.getKey(), bool);
                    } else if (object instanceof String str) {
                        valObj.addProperty(value.getKey(), str);
                    } else {
                        plugin.console(ComponentUtil.miniMessage("<red>Unsupported type of data tried to be saved! Only supports JsonElement, Number, Boolean, and String</red>"));
                        plugin.console(ComponentUtil.miniMessage("<red>지원하지 않는 타입의 데이터가 저장되려고 했습니다! JsonElement, Number, Boolean, String만 지원합니다</red>"));
                        data = backup;
                        return false;
                    }
                    if (data.contains(valObj)) {
                        data.set(key, valObj);
                    } else {
                        data.add(valObj);
                    }
                }
            }
            for (int i = toRemove.size() - 1; i >= 0; i--) {
                data.remove(toRemove.get(i));
            }
            needSave.lazySet(true);
            return true;
        }

        @Nullable
        protected List<JsonObject> get(Pair<String, Object> find) {
            Map<Integer, JsonObject> list = find(find);
            return new ArrayList<>(list.values());
        }

        private Map<Integer, JsonObject> find(Pair<String, Object> find) {
            Map<Integer, JsonObject> result = new HashMap<>();
            for (int i = 0; i < data.size(); i++) {
                JsonObject object = data.get(i).getAsJsonObject();
                if (find != null) {
                    JsonElement get = object.get(find.getKey());
                    if (get == null || get.isJsonNull()) continue;
                    if (!gson.fromJson(get, Object.class).equals(find.getValue())) continue;
                }
                result.put(i, object);
            }
            return result;
        }

        public boolean sync() {
            try {
                JsonElement json = JsonParser.parseReader(new FileReader(file));
                data = json != null && !json.isJsonNull() ? json.getAsJsonArray() : new JsonArray();
                return true;
            } catch (FileNotFoundException e) {
                plugin.console(ComponentUtil.miniMessage("<red>Error when sync " + file.getName() + "!</red>"));
                plugin.console(ComponentUtil.miniMessage("<red> " + file.getName() + " 파일과 동기화 도중 오류가 발생하였습니다!</red>"));
                return false;
            }
        }

        private void save() {
            try (Writer writer = new FileWriter(file)) {
                gson.newBuilder().setPrettyPrinting().create().toJson(data, writer);
            } catch (IOException e) {
                plugin.console(ComponentUtil.miniMessage("<red>Error when saving " + file.getName() + "!</red>"));
                plugin.console(ComponentUtil.miniMessage("<red> " + file.getName() + " 파일을 저장하는 도중 오류가 발생하였습니다!</red>"));
            }
        }

        protected void close() {
            task.cancel();
            save();
        }
    }
}
