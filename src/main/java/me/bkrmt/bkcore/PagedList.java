package me.bkrmt.bkcore;

import me.bkrmt.bkcore.bkgui.event.WindowResponse;
import me.bkrmt.bkcore.bkgui.gui.GUI;
import me.bkrmt.bkcore.bkgui.gui.Rows;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;
import me.bkrmt.bkcore.bkgui.page.Page;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PagedList {
    private List<Page> pages;
    private final Player player;
    private final BkPlugin plugin;
    private int listRowSize;
    private int listRows;
    private int startingSlot;
    private int[] navigationSlots;
    private List<WindowResponse> windowResponses;
    private final String identifier;
    private List<Page> availableSlotPages;
    private final ArrayDeque<PagedItem> itemList;
    private final Map<Long, ItemStorage> idStorage;
    private String guiTitle;
    private int totalPages;
    private Rows guiRows;
    private List<Integer> gridSlots;
    private Object customOptions;

    public PagedList(BkPlugin plugin, Player player, String identifier, ArrayDeque<PagedItem> itemList) {
        this.plugin = plugin;
        this.player = player;
        this.itemList = itemList;
        if (!identifier.contains(player.getName().toLowerCase())) {
            this.identifier = player.getName().toLowerCase() + "-" + identifier;
        } else {
            this.identifier = identifier;
        }
        gridSlots = null;
        idStorage = new HashMap<>();
        availableSlotPages = null;
        windowResponses = null;
        startingSlot = 10;
        navigationSlots = null;
        totalPages = -1;
        guiRows = Rows.SIX;
        guiTitle = " ";
        listRows = 4;
        listRowSize = 7;
    }

    public PagedList buildMenu() {
        pages = new ArrayList<>();

        totalPages = (int) Math.ceil((double) itemList.size() / (double) (listRowSize * listRows));
        AtomicInteger assignableID = new AtomicInteger(1);
        itemList.forEach(pagedItem -> {
            pagedItem.assignID(assignableID.getAndIncrement());
            if (!pagedItem.isIgnorePage() && pagedItem.getPage() > totalPages) totalPages = pagedItem.getPage();
        });

        Page previousPage = null;
        availableSlotPages = new ArrayList<>();
        for (int i = 1; i < totalPages + 1; i++) {
            Page newPage = new Page(plugin, plugin.getAnimatorManager(), new GUI(
                    guiTitle
                            .replace("{total-pages}", String.valueOf(totalPages))
                            .replace("{current-page}", String.valueOf(i))
                            .replace("{page-number}", String.valueOf(i))
                            .replace("{player}", (player != null ? player.getName() : "?"))
                    , guiRows
            ), i);

            if (windowResponses != null) windowResponses.forEach(newPage::addWindowResponse);
            if (navigationSlots != null) newPage.setButtonSlots(navigationSlots);

            if (previousPage != null) {
                newPage.setPreviousPage(previousPage);
                previousPage.setNextPage(newPage);
            }
            previousPage = newPage;
            pages.add(newPage);
            availableSlotPages.add(newPage);
        }

        buildGrid();

        while (itemList.peek() != null) {
            PagedItem pagedItem = itemList.poll();
            Page page;
            if (!pagedItem.isIgnorePage() && pagedItem.getPage() > 0) page = pages.get(pagedItem.getPage() - 1);
            else page = findAvailablePage().getPage();
            if (page != null) {
                int slot;
                if (!pagedItem.isIgnoreSlot() && !(pagedItem.getSlot() < 0)) {
                    if (pagedItem.getSlot() > page.getGui().getInventory().getSize()) slot = page.getGui().getInventory().getSize() - 1;
                    else slot = pagedItem.getSlot();
                } else slot = findNextGridSlot(page);
                if (pagedItem.getSlot() < 0 && slot == gridSlots.get(gridSlots.size()-1)) availableSlotPages.remove(0);

                if (!(slot < 0)) {
                    setDisplayItem(pagedItem, page, slot);
                    idStorage.put(pagedItem.getID(), new ItemStorage(page, slot));
                }
            }
        }
        return this;
    }

    private void setDisplayItem(PagedItem pagedItem, Page page, int slot) {
        String displayName = buildDisplayName(pagedItem, page);
        List<String> lore = buildLore(pagedItem, page);

        int pageNumber = page.getPageNumber();
        Object displayObject = pagedItem.getDisplayItem(this, page);
        if (displayObject instanceof ItemStack) {
            ItemBuilder pageItem = new ItemBuilder((ItemStack) displayObject)
                    .setName(displayName)
                    .setLore(lore)
                    .hideTags();

            page.pageSetItem(slot, pageItem, "paged-list-" + identifier + "-slot-" + slot + "-pag-" + pageNumber, pagedItem.getElementResponse(this, page));
        } else if (displayObject instanceof HeadDisplay) {
            HeadDisplay headDisplay = (HeadDisplay) displayObject;
            page.pageSetHead(slot, headDisplay.getOwner(), headDisplay.getDisplayName(), headDisplay.getLore(), "paged-list-" + identifier + "-slot-" + slot + "-pag-" + pageNumber, pagedItem.getElementResponse(this, page));
        }
    }

    private List<String> buildLore(PagedItem pagedItem, Page page) {
        int pageNumber = page.getPageNumber();
        List<String> itemLore = pagedItem.getLore(this, page);
        List<String> lore = new ArrayList<>();
        if (itemLore != null) {
            lore = itemLore.stream().map(s -> s.replace("{page-number}", String.valueOf(pageNumber).replace("{total-pages}", String.valueOf(totalPages)))).collect(Collectors.toList());
        }
        return lore;
    }

    private String buildDisplayName(PagedItem pagedItem, Page page) {
        int pageNumber = page.getPageNumber();
        return pagedItem.getDisplayName(this, page)
                .replace("{current-page}", String.valueOf(pageNumber)
                        .replace("{total-pages}", String.valueOf(totalPages)));
    }

    public void updateItem(long id, PagedItem newItem) {
        ItemStorage itemStorage = idStorage.get(id);
        if (itemStorage != null) {
            setDisplayItem(newItem, itemStorage.getPage(), itemStorage.getSlot());
        }
    }

    public List<Page> getAvailableSlotPages() {
        return availableSlotPages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public ItemStorage findAvailablePage() {
        int availableSlot;
        for (Page page : availableSlotPages) {
            availableSlot = findNextGridSlot(page);
            if (!(availableSlot < 0)) {
                return new ItemStorage(page, availableSlot);
            }
        }
        return null;
    }

    public int findNextGridSlot(Page page) {
        for (int slot : gridSlots) {
            if (page.getGui().getInventory().getItem(slot) == null) {
                return slot;
            }
        }
        return -1;
    }

    public ArrayDeque<PagedItem> getItemList() {
        return itemList;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Player getPlayer() {
        return player;
    }

    public int getStartingSlot() {
        return startingSlot;
    }

    public int[] getNavigationSlots() {
        return navigationSlots;
    }

    public List<Integer> getGridSlots() {
         return gridSlots;
    }

    private void buildGrid() {
        List<Integer> slots = new ArrayList<>();
        int slot = startingSlot;
        int add = getAdd();
        for (int c = 0; c < listRows; c++) {
            for (int x = 0; x < listRowSize; x++) {
                slots.add(slot);
                slot++;
            }
            slot = slot + add;
        }
        gridSlots = slots;
    }

    private int getAdd() {
        int add = 1;
        switch (startingSlot) {
            case 1:
            case 10:
                add = 2;
                break;
            case 2:
            case 11:
                add = 4;
                break;
            case 3:
            case 12:
                add = 6;
                break;
            case 4:
            case 13:
                add = 8;
                break;
            case 5:
            case 14:
                add = 10;
                break;
        }
        return add;
    }

    public PagedList setStartingSlot(int slot) {
        this.startingSlot = slot;
        return this;
    }

    public PagedList addWindowResponses(Object... responses) {
        this.windowResponses = new ArrayList<>();
        if (responses.length > 0) {
            for (Object response : responses) {
                if (response instanceof WindowResponse)
                    windowResponses.add((WindowResponse) response);
            }
        }
        return this;
    }

    public PagedList setButtonSlots(int previusPage, int nextPage) {
        navigationSlots = new int[]{previusPage, nextPage};
        return this;
    }

    public Object getCustomOptions() {
        return customOptions;
    }

    public PagedList setCustomOptions(Object customOptions) {
        this.customOptions = customOptions;
        return this;
    }

    public String getGuiTitle() {
        return guiTitle;
    }

    public Rows getGuiRows() {
        return guiRows;
    }

    public BkPlugin getPlugin() {
        return plugin;
    }

    public int getListRowSize() {
        return listRowSize;
    }

    public int getListRows() {
        return listRows;
    }

    public PagedList setGuiRows(Rows guiRows) {
        this.guiRows = guiRows;
        return this;
    }

    public PagedList setGuiTitle(String guiTitle) {
        this.guiTitle = guiTitle;
        return this;
    }

    public List<Page> getPages() {
        return pages;
    }

    public PagedList setListRowSize(int listRowSize) {
        this.listRowSize = listRowSize;
        return this;
    }

    public PagedList setListRows(int listRows) {
        this.listRows = listRows;
        return this;
    }

    public PagedList openPage(int page) {
        Page requestedPage = pages.get(page);
        if (requestedPage == null) {
            try {
                throw new NullPointerException("Page " + page + " does not exit. Pages size: " + pages.size());
            } catch (NullPointerException e) {
                e.printStackTrace();
                return this;
            }
        } else requestedPage.openGui(player);
        return this;
    }

}
