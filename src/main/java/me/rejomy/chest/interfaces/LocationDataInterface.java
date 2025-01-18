package me.rejomy.chest.interfaces;

import org.bukkit.Location;

import java.util.HashMap;

public interface LocationDataInterface {

    void add(Location location, String name);
    void remove(Location location, String name);
    HashMap<Location, String> get();

}
