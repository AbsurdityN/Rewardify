package me.absurdity.apanel.gui;

import me.absurdity.apanel.main;
import me.absurdity.apanel.data.playerDataStore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DailyRewardGUI {

    private final main plugin;
    private final playerDataStore playerData;
    private static final long DAY_LENGTH = 24 * 60 * 60 * 1000L;
    private static final long HOUR_LENGTH = 60 * 60 * 1000L;
    private static final long MINUTE_LENGTH = 60 * 1000L;

    public DailyRewardGUI(main plugin) {
        this.plugin = plugin;
        this.playerData = new playerDataStore(plugin);
    }

    public void openGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "Rewards");

        FileConfiguration rewards = plugin.getRewardsConfig();
        long lastClaim = playerData.getLastClaim(p);
        int streak = playerData.getStreak(p);
        long currentTime = System.currentTimeMillis();

        for (int day = 1; day <= 7; day++) {
            String path = "daily-rewards.day" + day;
            if (!rewards.contains(path)) continue;

            int slot = rewards.getInt(path + ".slot", day - 1);
            boolean isClaimed = day <= streak && currentTime - lastClaim < DAY_LENGTH;

            ItemStack guiItem;
            ItemMeta meta;

            if (isClaimed) {
                Material claimedMaterial = Material.valueOf(rewards.getString(path + ".claimed-material", "BARRIER"));
                guiItem = new ItemStack(claimedMaterial, 1);
                meta = guiItem.getItemMeta();
                if (meta != null) {
                    String displayName = rewards.getString(path + ".claimed-display-name", "&cAlready Claimed");
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

                    if (rewards.contains(path + ".claimed-lore")) {
                        List<String> lore = new ArrayList<>(rewards.getStringList(path + ".claimed-lore"));

                        long timeLeft = (lastClaim + DAY_LENGTH) - currentTime;
                        long hours = timeLeft / HOUR_LENGTH;
                        long minutes = (timeLeft % HOUR_LENGTH) / MINUTE_LENGTH;

                        lore.replaceAll(line -> ChatColor.translateAlternateColorCodes('&',
                                line.replace("%hours%", String.valueOf(hours))
                                        .replace("%minutes%", String.valueOf(minutes))));
                        meta.setLore(lore);
                    }
                    guiItem.setItemMeta(meta);
                }

            } else {

                Material unclaimedMaterial = Material.valueOf(rewards.getString(path + ".unclaimed-material", "STONE"));
                int amount = rewards.getInt(path + ".amount", 1);
                guiItem = new ItemStack(unclaimedMaterial, amount);
                meta = guiItem.getItemMeta();
                if (meta != null) {
                    if (rewards.contains(path + ".unclaimed-display-name")) {
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                rewards.getString(path + ".unclaimed-display-name")));
                    }
                    if (rewards.contains(path + ".unclaimed-lore")) {
                        List<String> lore = new ArrayList<>(rewards.getStringList(path + ".unclaimed-lore"));
                        lore.replaceAll(line -> ChatColor.translateAlternateColorCodes('&', line));
                        meta.setLore(lore);
                    }
                    guiItem.setItemMeta(meta);
                }
            }

            inv.setItem(slot, guiItem);
        }

        p.openInventory(inv);
    }
}
