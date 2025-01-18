package me.rejomy.chest.util;

import com.sun.org.apache.xalan.internal.xsltc.trax.XSLTCSource;
import me.rejomy.chest.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

import static me.rejomy.chest.util.RandomUtil.getRandom;
import static me.rejomy.chest.util.file.NumberUtil.parseInt;

public class ChestUtil {
    public List<Chest> chests;

    public void reset(Inventory inventory, ArrayList<CustomItemStack> items, int min, int max) {
        items = new ArrayList<>(items);

        List<Integer> slots = new ArrayList<>();

        for(int value = 0; value < inventory.getSize(); value++) {
            inventory.setItem(value, null);
            slots.add(value);
        }

        int itemAmount = getRandom(max, min);

        for(byte i = 0; i < itemAmount; i++) {
            int slot = slots.remove(RandomUtil.RANDOM.nextInt(slots.size()));

            CustomItemStack customItem = items.remove(RandomUtil.RANDOM.nextInt(items.size()));
            ItemStack item = customItem.item.clone();

            {
                int amount = getRandom(customItem.max, customItem.min);
                item.setAmount(Math.min(item.getMaxStackSize(), amount));
            }

            if (customItem.durMax != 0) {
                int durability = RandomUtil.getRandom(customItem.durMax, customItem.durMin);
                item.setDurability((short) ((item.getType().getMaxDurability() * durability) / 100));
            }

            if (!customItem.enchants.isEmpty()) {
                int enchantsAmount = customItem.randomEnchants ? RandomUtil.RANDOM.nextInt(customItem.enchants.size())
                        : customItem.enchants.size();

                // Значит что секция была определена в конфиге
                if(customItem.maxEncAmount >= 0 && customItem.minEncAmount >= 0) {
                    enchantsAmount = RandomUtil.getRandom(customItem.maxEncAmount, customItem.minEncAmount);
                }

                List<Map.Entry<Enchantment, int[]>> entryList = new ArrayList<>(customItem.enchants.entrySet());
                Collections.shuffle(entryList);

                for (Map.Entry<Enchantment, int[]> map : entryList) {
                    if (enchantsAmount-- <= 0) {
                        break;
                    }

                    int[] levels = map.getValue();

                    int min1 = Arrays.stream(levels).min().getAsInt();
                    int max1 = Arrays.stream(levels).max().getAsInt();

                    int enchantLevel = getRandom(max1, min1, 0.4f);

                    item.addUnsafeEnchantment(map.getKey(), enchantLevel);
                }
            }

            inventory.setItem(slot, item);
        }
    }

    public void fill() {
        List<Chest> chests = new ArrayList<>();
        FileConfiguration config = Main.getInstance().getConfig();

        for(String section : config.getConfigurationSection("chests").getKeys(false)) {
            Chest chest = new Chest();
            chest.name = section;

            String link = "chests." + section + ".";

            int[] respawnSec = getAmount(config.getString(link + "respawn-seconds").split("-"));

            chest.resetTicksMax = Arrays.stream(respawnSec).max().getAsInt() * 20;
            chest.resetTicksMin = Arrays.stream(respawnSec).min().getAsInt() * 20;

            {
                String[] configAmount = config.getString(link + "amount").split("-");
                int[] amount = getAmount(configAmount);

                if (amount == null) {
                    Main.getInstance().getLogger().severe("Chest " + chest + " has incorrect amount. Amount=" +
                            config.getString(link + "amount"));
                    continue;
                }

                chest.min = Arrays.stream(amount).min().getAsInt();
                chest.max = Arrays.stream(amount).max().getAsInt();
            }

            for(Map<?, ?> map : config.getMapList(link + "items")) {
                String[] sAmount = (String.valueOf(map.get("amount"))).split("-");

                int[] amount = getAmount(sAmount);
                int min = Arrays.stream(amount).min().getAsInt();
                int max = Arrays.stream(amount).max().getAsInt();

                Object id = map.get("id");
                Material material = isInt(id)? Material.getMaterial((Integer) id) : Material.valueOf((String) id);

                ItemStack item = new ItemStack(material);

                int durMin = 0, durMax = 0;

                if(map.containsKey("durability percent")) {
                    int[] durPercent = getAmount(((String) map.get("durability percent")).split("-"));
                    IntSummaryStatistics stats = Arrays.stream(durPercent).summaryStatistics();
                    durMax = stats.getMax();
                    durMin = stats.getMin();
                } else {
                    if(map.containsKey("data")) {
                        String dataC = String.valueOf(map.get("data"));
                        short data = (short) parseInt(dataC);
                        item.setDurability(data);
                    }
                }

                ItemMeta meta = item.getItemMeta();

                if(map.containsKey("item flags")) {
                    List<String> flags = (List<String>) map.get("item flags");

                    for(String flag : flags) {
                        meta.addItemFlags(ItemFlag.valueOf(flag));
                    }
                }

                if(map.containsKey("name")) {
                    meta.setDisplayName(ColorUtil.toColor((String) map.get("name")));
                }

                if(map.containsKey("lore")) {
                    List<String> lore = (List<String>) map.get("lore");

                    lore.replaceAll(ColorUtil::toColor);

                    meta.setLore(lore);
                }

                CustomItemStack customItem = new CustomItemStack(item, min, max);

                item.setItemMeta(meta);

                if(map.containsKey("enchants")) {
                    LinkedHashMap<String, Object> enchantSec = (LinkedHashMap<String, Object>) map.get("enchants");

                    if(enchantSec.get("enchants-amount") != null) {
                        String[] sEncAmounts = ((String) enchantSec.get("enchants-amount")).split("-");
                        int[] encAmounts = getAmount(sEncAmounts);
                        int minEncAm = Arrays.stream(encAmounts).min().getAsInt();
                        int maxEncAm = Arrays.stream(encAmounts).max().getAsInt();

                        customItem.minEncAmount = minEncAm;
                        customItem.maxEncAmount = maxEncAm;
                    }

                    if(enchantSec.get("current") != null) {
                        List<String> enchantsList = (List<String>) enchantSec.get("current");

                        for (String enchantInfo : enchantsList) {
                            if(enchantInfo.equals("all")) {
                                Enchantment[] enchants = Enchantment.values();

                                for(Enchantment enchantment : enchants) {
                                    if(enchantment.canEnchantItem(item)) {
                                        customItem.enchants.put(enchantment,
                                                new int[]{1, enchantment.getMaxLevel()});
                                    }
                                }

                                break;
                            }

                            String[] enchant = enchantInfo.split(":");

                            if (enchant.length == 1) {
                                customItem.enchants.put(Enchantment.getByName(enchant[0]), new int[]{1});
                            } else if (enchant.length == 2) {
                                int[] levels = getAmount(enchant[1].split("-"));
                                customItem.enchants.put(Enchantment.getByName(enchant[0]), levels);
                            }

                        }

                        customItem.randomEnchants = enchantSec.get("random") != null && (boolean) enchantSec.get("random");
                    }

                }

                customItem.durMin = durMin;
                customItem.durMax = durMax;

                chest.items.add(customItem);
            }

            chests.add(chest);
        }

        this.chests = chests;
    }

    private int[] getAmount(String[] cAmount) {
        try {
            int[] amount = new int[2];
            int value1 = Integer.parseInt(cAmount[0]);

            amount[0] = value1;

            if(cAmount.length == 1) {
                amount[1] = value1;
            } else {
                amount[1] = Integer.parseInt(cAmount[1]);
            }

            return amount;
        } catch (NumberFormatException exception) {
            return null;
        }

    }

    private boolean isInt(Object o) {
        return o instanceof Integer;
    }

}
