package me.bkrmt.bkcore;

import me.bkrmt.bkcore.bkgui.page.Page;
import me.bkrmt.bkcore.bkgui.event.ElementResponse;

import java.util.List;

public interface PagedItem {
    String getDisplayName(PagedList list, Page currentPage);

    int getSlot();

    int getPage();

    void setIgnorePage(boolean ignorePage);

    void setIgnoreSlot(boolean ignoreSlot);

    boolean isIgnorePage();

    boolean isIgnoreSlot();

    void setPage(int page);

    void setSlot(int slot);

    List<String> getLore(PagedList list, Page currentPage);

    Object getDisplayItem(PagedList list, Page currentPage);

    ElementResponse getElementResponse(PagedList list, Page currentPage);

    void assignID(long id);

    long getID();
}
