package me.bkrmt.bkcore;

import me.bkrmt.bkcore.bkgui.page.Page;
import me.bkrmt.bkcore.properties.DisplayItemBuilder;
import me.bkrmt.bkcore.properties.DisplayLoreBuilder;
import me.bkrmt.bkcore.properties.DisplayNameBuilder;

import java.util.List;

public abstract class AbstractItem implements PagedItem {
    private long id;
    private boolean ignorePage;
    private boolean ignoreSlot;
    private int page;
    private int slot;
    private String displayName;
    private Object displayItem;
    private List<String> lore;
    protected final Properties properties;

    protected AbstractItem(int slot, int page, DisplayNameBuilder displayName, DisplayLoreBuilder lore, DisplayItemBuilder displayItem) {
        this.page = page;
        this.slot = slot;
        this.properties = new Properties(displayName, displayItem, lore);
        updateDisplayName();
        updateDisplayItem();
        updateLore();
    }

    public Properties getProperties() {
        return properties;
    }

    public void updateDisplayName() {
        this.displayName = properties.getDisplayName().buildName();
    }

    public void updateLore() {
        this.lore = properties.getLore().buildLore();
    }

    public void updateDisplayItem() {
        this.displayItem = properties.getDisplayItem().buildItem();
    }

    @Override
    public String getDisplayName(PagedList list, Page currentPage) {
        return displayName;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setIgnorePage(boolean ignorePage) {
        this.ignorePage = ignorePage;
    }

    @Override
    public void setIgnoreSlot(boolean ignoreSlot) {
        this.ignoreSlot = ignoreSlot;
    }

    @Override
    public boolean isIgnorePage() {
        return ignorePage;
    }

    @Override
    public boolean isIgnoreSlot() {
        return ignoreSlot;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public List<String> getLore(PagedList list, Page currentPage) {
        return lore;
    }

    @Override
    public Object getDisplayItem(PagedList list, Page currentPage) {
        return displayItem;
    }

    @Override
    public void assignID(long id) {
        this.id = id;
    }

    @Override
    public long getID() {
        return id;
    }
}
