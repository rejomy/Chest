package me.rejomy.chest;

import me.rejomy.chest.command.ChestCommand;
import me.rejomy.chest.listener.InteractListener;
import me.rejomy.chest.util.Chest;
import me.rejomy.chest.util.ChestUtil;
import me.rejomy.chest.util.file.LocationFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {
    private static Main instance;
    public static Main getInstance() {
        return instance;
    }
    public HashMap<Location, Chest> CHEST_MAP = new HashMap<>();
    public LocationFile getLocations() {
        return locations;
    }
    private LocationFile locations;
    public ChestUtil getChestUtil() {
        return chestUtil;
    }
    private ChestUtil chestUtil = new ChestUtil();

    @Override
    public void onLoad() {
        instance = this;

        saveDefaultConfig();

        locations = new LocationFile();
        chestUtil.fill();
    }

    @Override
    public void onEnable() {
        getCommand("chest").setExecutor(new ChestCommand());

        Bukkit.getPluginManager().registerEvents(new InteractListener(), this);

        for(Map.Entry<Location, String> map : locations.get().entrySet()) {
            CHEST_MAP.put(map.getKey(), new Chest(
                    chestUtil.chests.stream().filter(chest -> chest.name.equals(map.getValue())).findFirst().get())
            );
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void fillMap() {
        // TODO:
    }

}
