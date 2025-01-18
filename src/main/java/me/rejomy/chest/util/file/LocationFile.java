package me.rejomy.chest.util.file;

import me.rejomy.chest.interfaces.LocationDataInterface;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationFile extends AbstractFile implements LocationDataInterface {

    public LocationFile() {
        super("location");

        createIfNotExistsFile();
        loadConfig();

        {
            List<String> list = new ArrayList<>();
            VALUES.put("locations", list);
        }

        setValuesIfNotExists();
        saveConfig();

    }

    @Override
    public void add(Location location, String name) {

        int x = location.getBlockX(),
                y = location.getBlockY(),
                z = location.getBlockZ();

        String loc = location.getWorld().getName() + " " + x + " "
                + y + " " + z + " " + name;

        remove(location, name);

        List<String> list = config.getStringList("locations");

        list.add(loc);

        config.set("locations", list);
        saveConfig();
    }

    @Override
    public void remove(Location location, String name) {
        List<String> list = config.getStringList("locations");

        int x = location.getBlockX(),
                y = location.getBlockY(),
                z = location.getBlockZ();

        String loc = location.getWorld().getName() + " " + x + " "
                + y + " " + z + " " + name;

        list.removeIf(currentString -> currentString.equals(loc));

        config.set("locations", list);

        saveConfig();
    }

    @Override
    public HashMap<Location, String> get() {
        HashMap<Location, String> map = new HashMap<>();

        List<String> locations = config.getStringList("locations");

        for(String location : locations) {
            String[] parts = location.split(" ");

            World world = Bukkit.getWorld(parts[0]);

            int x = Integer.parseInt(parts[1]),
                    y = Integer.parseInt(parts[2]),
                    z = Integer.parseInt(parts[3]);

            String name = parts[4];

            map.put(new Location(world, x, y, z), name);
        }

        return map;
    }

}
