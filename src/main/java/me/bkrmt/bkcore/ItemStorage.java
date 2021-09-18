package me.bkrmt.bkcore;

import me.bkrmt.bkcore.bkgui.page.Page;

public class ItemStorage {
    private final Page page;
    private final int slot;

    public ItemStorage(Page page, int availableSlot) {
        this.page = page;
        this.slot = availableSlot;
    }

    public Page getPage() {
        return page;
    }

    public int getSlot() {
        return slot;
    }
}
