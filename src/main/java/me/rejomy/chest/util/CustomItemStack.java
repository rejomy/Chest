package me.rejomy.chest.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CustomItemStack {
    public int min, max, durMin, durMax, minEncAmount = -1, maxEncAmount = -1;
    public HashMap<Enchantment, int[]> enchants = new HashMap<>();
    boolean randomEnchants = false;
    public ItemStack item;

    public CustomItemStack(ItemStack itemStack, int min, int max) {
        this.item = itemStack;
        this.max = max;
        this.min = min;
    }

}
