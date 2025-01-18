package me.rejomy.chest.util;

import me.rejomy.chest.Main;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class Chest {
    private int lastResetTicks;
    public int resetTicksMin;
    public int resetTicksMax;
    public long lastResetTime;
    public ArrayList<CustomItemStack> items = new ArrayList<>();
    public boolean isReset = false;
    public int min, max;
    public String name;

    public Chest(Chest chest) {
        this.resetTicksMin = chest.resetTicksMin;
        this.resetTicksMax = chest.resetTicksMax;
        this.lastResetTime = chest.lastResetTime;
        this.min = chest.min;
        this.max = chest.max;
        this.items = chest.items;
        this.name = chest.name;
        this.isReset = chest.isReset;
    }

    public Chest() {}

    public void reset(Inventory inventory) {
        isReset = true;
        boolean useTimer = System.currentTimeMillis() - lastResetTime < lastResetTicks * 50L;
        lastResetTicks = RandomUtil.getRandom(resetTicksMax, resetTicksMin);

        if(!useTimer) {
            Main.getInstance().getChestUtil().reset(inventory, items, min, max);
            isReset = false;
            lastResetTime = System.currentTimeMillis();
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            Main.getInstance().getChestUtil().reset(inventory, items, min, max);
            isReset = false;
            lastResetTime = System.currentTimeMillis();
        }, lastResetTicks);
    }
}
