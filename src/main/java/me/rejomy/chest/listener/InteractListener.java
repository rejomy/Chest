package me.rejomy.chest.listener;

import me.rejomy.chest.Main;
import me.rejomy.chest.util.Chest;
import me.rejomy.chest.util.ColorUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class InteractListener implements Listener {
    HashMap<Location, Chest> CHEST_MAP = Main.getInstance().CHEST_MAP;

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || block == null || block.getType() != Material.CHEST
                && block.getType() != Material.TRAPPED_CHEST) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();

        if (itemInHand != null && itemInHand.hasItemMeta()) {
            ItemMeta meta = itemInHand.getItemMeta();

            if (meta.hasDisplayName() &&
                    meta.getDisplayName().contains("eChest create stick")) {
                List<String> lore = meta.getLore();

                if (meta.hasLore() && lore.size() == 4) {
                    String name = lore.get(2).replaceAll(".+: .?e", "");

                    Main.getInstance().getLocations().add(block.getLocation(), name);

                    Chest chest = new Chest(Main.getInstance().getChestUtil().chests
                            .stream().filter(t -> t.name.equals(name)).findFirst().get());

                    Main.getInstance().CHEST_MAP.put(block.getLocation(), chest);

                    player.sendMessage(ColorUtil.toColor("&8       &m-----&f\n &aChest has been created!\n&8       &m-----"));

                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (!CHEST_MAP.containsKey(block.getLocation()) || CHEST_MAP.get(block.getLocation()).isReset) return;

        Chest chest = CHEST_MAP.get(block.getLocation());

        Inventory inventory = ((org.bukkit.block.Chest) block.getState()).getInventory();
        chest.reset(inventory);
    }
}
