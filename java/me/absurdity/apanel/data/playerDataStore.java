package me.absurdity.apanel.data;

import me.absurdity.apanel.main;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class playerDataStore {

    private NamespacedKey lastClaimKey;
    private NamespacedKey streakKey;


    public playerDataStore(main plugin) {
        this.lastClaimKey = new NamespacedKey(plugin, "lastClaimTime");
        this.streakKey = new NamespacedKey(plugin, "streakDay");
    }

    public void saveData(Player player, long lastClaim, int streak) {
        player.getPersistentDataContainer().set(lastClaimKey, PersistentDataType.LONG, lastClaim);
        player.getPersistentDataContainer().set(streakKey, PersistentDataType.INTEGER, streak);
    }

    public int getLastClaim(Player player) {
        return player.getPersistentDataContainer().getOrDefault(lastClaimKey, PersistentDataType.INTEGER, 0);
    }

    public int getStreak(Player player) {
        return player.getPersistentDataContainer().getOrDefault(streakKey, PersistentDataType.INTEGER, 0);
    }
}
