package me.rejomy.chest.util.file;

import me.rejomy.chest.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFile {

    protected File file;
    protected YamlConfiguration config;
    protected HashMap<String, Object> VALUES = new HashMap<>();

    public AbstractFile(File file, YamlConfiguration config) {
        this.file = file;
        this.config = config;
    }

    public AbstractFile(String name) {
        this.file = new File(Main.getInstance().getDataFolder(), name.replace(".yml", "") + ".yml");
    }

    public void setValuesIfNotExists() {

        for(Map.Entry<String, Object> map : VALUES.entrySet()) {
            if(config.get(map.getKey()) != null) continue;
            
            config.set(map.getKey(), map.getValue());
        }

    }

    public void createIfNotExistsFile() {
        if(!file.exists()) return;

        try {
            file.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void loadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

}
