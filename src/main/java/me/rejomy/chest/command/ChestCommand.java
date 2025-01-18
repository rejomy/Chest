package me.rejomy.chest.command;

import me.rejomy.chest.Main;
import me.rejomy.chest.util.Chest;
import me.rejomy.chest.util.ColorUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChestCommand implements CommandExecutor {

    public ItemStack createItem(String chestName) {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtil.toColor("&eChest create stick"));

        {
            List<String> lore = new ArrayList<>();
            lore.add(BORDER);
            lore.add(ColorUtil.toColor(" &fClick the stick to chest for select."));
            lore.add(ColorUtil.toColor(" &7Chest type: &e" + chestName));
            lore.add(BORDER);
            meta.setLore(lore);
        }

        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);

        return item;
    }

    private final String BORDER = ColorUtil.toColor("&8            &m-------");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sendInfo(sender);
            return false;
        }

        switch (args[0]) {

            case "create":
                if (!isCorrect(sender, args, 2, true)) {
                    return false;
                }

                String chestName = args[1];

                if (Main.getInstance().getConfig().getConfigurationSection("chests." + chestName) == null) {
                    sendMessage(sender, " &8Error: &7Chest " + chestName + " not found in the plugin config.");
                    return false;
                }

                Player player = (Player) sender;

                player.getInventory().addItem(createItem(chestName));

                sendMessage(sender,
                        " &fYou received a stick to create chest " + chestName + ".\n" +
                                " &fRight click to the chest with the stick."
                );
                break;

            case "list":

                if (Main.getInstance().getChestUtil().chests.isEmpty()) {
                    sendMessage(sender, " &fNo chests found.");
                } else {
                    sender.sendMessage(BORDER);
                    for (Chest chest : Main.getInstance().getChestUtil().chests) {
                        sender.sendMessage(ColorUtil.toColor(" &7Chest: &f" + chest.name));
                    }
                    sender.sendMessage(BORDER);
                }
                break;

            case "location":
                if (Main.getInstance().CHEST_MAP.size() == 0) {
                    sendMessage(sender, " &fNo locations found.");
                } else {
                    sender.sendMessage(BORDER);

                    for (Map.Entry<Location, Chest> map : Main.getInstance().CHEST_MAP.entrySet()) {
                        Location location = map.getKey();

                        sender.sendMessage(ColorUtil.toColor(" &7Position: &f" +
                                location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ()
                                + " &7Chest: &e" + map.getValue().name
                        ));

                    }
                    sender.sendMessage(BORDER);
                }
                break;

            default:
                sendInfo(sender);
        }

        return false;
    }

    public boolean isCorrect(CommandSender sender, String[] args, int argsLength, boolean onlyPlayer) {
        if (args.length != argsLength) {
            sendMessage(sender, " &8Error: &7Args length.\n &7Reason: &f" + args.length + "&8/&f" + argsLength);
            return false;
        }

        if (onlyPlayer && sender instanceof ConsoleCommandSender) {
            sendMessage(sender, " &8Error: &7This command can only be executed by a player.\n &7Reason: "
                    + "&fYou are not a player!");
            return false;
        }

        return true;
    }

    public void sendMessage(CommandSender sender, String message) {

        sender.sendMessage(
                BORDER + "\n"
                        + ColorUtil.toColor("&7" + message) +
                        "\n" + BORDER
        );

    }

    public void sendInfo(CommandSender sender) {
        sendMessage(sender, "&f Chest count: &e" + Main.getInstance().CHEST_MAP.size() +
                "\n &f/chest list &8- &7List of a chests name." +
                "\n &f/chest location &8- &7List of a chest locations." +
                "\n &f/chest create name &8- &7Create a chest.");
    }

}
