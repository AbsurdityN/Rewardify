package me.absurdity.apanel;


import me.absurdity.apanel.gui.DailyRewardGUI;
import me.absurdity.apanel.listeners.GUIClickEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.Executor;

public final class main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("AdminPanel Enabled");

        this.getCommand("daily").setExecutor(new DailyRewardGUI(this));
        getServer().getPluginManager().registerEvents(new GUIClickEvent(this), this);

        saveDefaultConfig();
        saveResource("config.yml", false);
        saveResource("messages.yml", true);
        reloadRewards();
        }

        private FileConfiguration rewardsConfig;

        public void reloadRewards() {
            File rewardsFile = new File(getDataFolder(), "rewards.yml");
            File messagesFile = new File(getDataFolder(), "messages.yml");
            rewardsConfig = YamlConfiguration.loadConfiguration(rewardsFile);
        }

    public FileConfiguration getRewardsConfig() {
        return rewardsConfig;
    }

    @Override
        public void onDisable() {
        getLogger().info("AdminPanel Enabled");
    }

    }

