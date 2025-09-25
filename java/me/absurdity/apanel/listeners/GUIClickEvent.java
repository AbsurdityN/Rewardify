package me.absurdity.apanel.listeners;

import me.absurdity.apanel.main;
import me.absurdity.apanel.data.playerDataStore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GUIClickEvent implements Listener {

    private final main plugin;
    private final playerDataStore playerData;
    private static final long DAY_LENGTH = 24 * 60 * 60 * 1000L;


    public GUIClickEvent(main plugin) {
        this.plugin = plugin;
        this.playerData = new playerDataStore(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Rewards")) return;
        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        Player p = (Player) e.getWhoClicked();
        FileConfiguration rewards = plugin.getRewardsConfig();
        FileConfiguration messages = plugin.getMessagesConfig();

        long currentTime = System.currentTimeMillis();
        long lastClaim = playerData.getLastClaim(p);
        int streak = playerData.getStreak(p);

        // check if already claimed
        if (currentTime - lastClaim < DAY_LENGTH) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    messages.getString("already-claimed-message", "&cYou already claimed this reward!")));
            p.closeInventory();
            return;
        }

        streak++;
        if (streak > 7) streak = 1;

        String path = "daily-rewards.day" + streak;
        if (!rewards.contains(path)) {
            p.sendMessage(ChatColor.RED + "No reward configured for day " + streak);
            return;
        }

        // give reward
        if (rewards.contains(path + ".reward")) {
            Material rewardMaterial = Material.valueOf(rewards.getString(path + ".reward", "STONE"));
            int amount = rewards.getInt(path + ".amount", 1);
            p.getInventory().addItem(new ItemStack(rewardMaterial, amount));
        } else if (rewards.contains(path + ".reward-command")) {
            String command = rewards.getString(path + ".reward-command", "")
                    .replace("%player%", p.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        // success message
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                messages.getString("claim-message", "&aYou claimed your daily reward!")));

        // update claimed slot in GUI
        int slot = rewards.getInt(path + ".slot", streak);
        Material claimedMaterial = Material.valueOf(rewards.getString(path + ".claimed-material", "BARRIER"));
        ItemStack claimedItem = new ItemStack(claimedMaterial, 1);

        ItemMeta meta = claimedItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    rewards.getString(path + ".claimed-display-name", "&cAlready Claimed")));
            if (rewards.contains(path + ".claimed-lore")) {
                List<String> lore = rewards.getStringList(path + ".claimed-lore");
                lore.replaceAll(line -> ChatColor.translateAlternateColorCodes('&', line));
                meta.setLore(lore);
            }
            claimedItem.setItemMeta(meta);

        }

        p.getOpenInventory().getTopInventory().setItem(slot, claimedItem);

        // save data
        playerData.saveData(p, currentTime, streak);
    }
}
