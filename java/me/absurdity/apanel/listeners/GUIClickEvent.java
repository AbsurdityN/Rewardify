package me.absurdity.apanel.listeners;

import me.absurdity.apanel.main;
import me.absurdity.apanel.data.playerDataStore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


public class GUIClickEvent implements Listener {

    private final main plugin;
    private static final long DAY_LENGTH = 24 * 60 * 60 * 1000L;

    public GUIClickEvent(main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Rewards")) return;
        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        Player p = (Player) e.getWhoClicked();
        playerDataStore playerData = new playerDataStore(plugin); // instance of your persistent data handler

        long currentTime = System.currentTimeMillis();
        long lastClaim = playerData.getLastClaim(p);
        int currentStreak = playerData.getStreak(p);

        // Check if 24 hours have passed
        if (currentTime - lastClaim < DAY_LENGTH) {
            p.sendMessage(ChatColor.RED + "You already claimed your daily reward. Come back later!");
            p.closeInventory();
            return;
        }

        // Increment streak
        currentStreak++;
        if (currentStreak > 7) currentStreak = 1; // reset after 7 days

        FileConfiguration rewards = plugin.getRewardsConfig();
        String path = "daily-rewards.day" + currentStreak; // fixed to match your config

        if (rewards.contains(path)) {
            // Get reward info
            Material rewardMaterial = Material.valueOf(rewards.getString(path + ".reward"));
            int amount = rewards.getInt(path + ".amount");
            String msg = rewards.getString(path + ".claim-message");
            Material claimedMaterial = Material.valueOf(rewards.getString(path + ".claimed-material"));
            int slot = rewards.getInt(path + ".slot");

            // Give reward
            p.getInventory().addItem(new ItemStack(rewardMaterial, amount));
            assert msg != null;
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

            // Update GUI
            ItemStack claimedItem = new ItemStack(claimedMaterial, 1);
            p.getOpenInventory().getTopInventory().setItem(slot, claimedItem);

            // Save new claim time and streak
            playerData.saveData(p, currentTime, currentStreak);

        } else {
            p.sendMessage(ChatColor.RED + "No reward configured for day " + currentStreak + "!");
        }
    }
}
