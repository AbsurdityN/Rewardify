package me.absurdity.apanel;

import me.absurdity.apanel.gui.DailyRewardGUI;
import me.absurdity.apanel.listeners.GUIClickEvent;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class main extends JavaPlugin {

    private FileConfiguration rewardsConfig;
    private FileConfiguration messagesConfig;

    @Override
    public void onEnable() {
        getLogger().info("DailyRewards Enabled");

        // Register command + GUI
        this.getCommand("daily").setExecutor((CommandExecutor) new DailyRewardGUI(this));
        getServer().getPluginManager().registerEvents(new GUIClickEvent(this), this);

        // Save config files if not present
        saveDefaultConfig();
        saveResource("messages.yml", false);

        loadRewardsConfig();
        loadMessagesConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("Daily Rewards Disabled");
    }


    public FileConfiguration getRewardsConfig() {
        return rewardsConfig;
    }

    private void loadRewardsConfig() {
        File file = new File(getDataFolder(), "config.yml");
        rewardsConfig = YamlConfiguration.loadConfiguration(file);
    }


    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    private void loadMessagesConfig() {
        File file = new File(getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    public void reloadRewards() {
        loadRewardsConfig();
    }

    public void reloadMessages() {
        loadMessagesConfig();
    }
}
