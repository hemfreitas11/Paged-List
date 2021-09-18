package me.bkrmt.bkcore;

import me.bkrmt.bkcore.properties.DisplayItemBuilder;
import me.bkrmt.bkcore.properties.DisplayLoreBuilder;
import me.bkrmt.bkcore.properties.DisplayNameBuilder;

public class Properties {
    private DisplayNameBuilder displayName;
    private DisplayItemBuilder displayItem;
    private DisplayLoreBuilder lore;

    public Properties(DisplayNameBuilder displayName, DisplayItemBuilder displayItem, DisplayLoreBuilder lore) {
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.lore = lore;
    }

    public DisplayNameBuilder getDisplayName() {
        return displayName;
    }

    public void setDisplayName(DisplayNameBuilder displayName) {
        this.displayName = displayName;
    }

    public DisplayItemBuilder getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(DisplayItemBuilder displayItem) {
        this.displayItem = displayItem;
    }

    public DisplayLoreBuilder getLore() {
        return lore;
    }

    public void setLore(DisplayLoreBuilder lore) {
        this.lore = lore;
    }
}
