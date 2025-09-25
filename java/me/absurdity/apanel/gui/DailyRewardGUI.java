package me.absurdity.apanel.gui;

import me.absurdity.apanel.data.playerDataStore;
import me.absurdity.apanel.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DailyRewardGUI implements CommandExecutor {

    private final main plugin;

    public DailyRewardGUI(main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You need to be a player to run this command.");
            return true;
        }

        Player player = (Player) sender;
        Inventory inv = createRewardGUI(player);
        player.openInventory(inv);
        return true;
    }

    private Inventory createRewardGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "Rewards");
        FileConfiguration rewardsConfig = plugin.getRewardsConfig();
        playerDataStore data = new playerDataStore(plugin);
        int currentStreak = data.getStreak(player);

        // Loop through 7 days
        for (int day = 1; day <= 7; day++) {
            String path = "daily-rewards.day" + day;
            if (!rewardsConfig.contains(path)) continue;

            Material unclaimedMat = Material.valueOf(rewardsConfig.getString(path + ".unclaimed-material"));
            Material claimedMat = Material.valueOf(rewardsConfig.getString(path + ".claimed-material"));
            int slot = rewardsConfig.getInt(path + ".slot");
            String displayName = rewardsConfig.getString(path + ".display-name");

            ItemStack item;
            if (day <= currentStreak) {
                item = new ItemStack(claimedMat, 1);
            } else {
                item = new ItemStack(unclaimedMat, 1);
            }

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                assert displayName != null;
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
                item.setItemMeta(meta);
            }

            if (slot >= 0 && slot < inv.getSize()) {
                inv.setItem(slot, item);
            }
        }

        return inv;
    }
}


