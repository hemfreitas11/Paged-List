package me.bkrmt.bkcore;

import org.bukkit.OfflinePlayer;

import java.util.List;

public class HeadDisplay {
    OfflinePlayer owner;
    String displayName;
    List<String> lore;

    public HeadDisplay(OfflinePlayer owner, String displayName, List<String> lore) {
        this.owner = owner;
        this.displayName = displayName;
        this.lore = lore;
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }
}
