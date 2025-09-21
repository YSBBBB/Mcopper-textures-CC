package inventorypreviewpatch.configs.utils;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

import static inventorypreviewpatch.event.ResourcesLoadedListener.isLoadedWuTongUI;

public enum InventoryPreviewFixMode implements IConfigOptionListEntry {
    NO             ("no", "inventorypreviewpatch.label.InventoryPreviewFixMode.no"),
    VANILLA        ("vanilla", "inventorypreviewpatch.label.InventoryPreviewFixMode.vanilla"),
    WUTONG         ("wutong", "inventorypreviewpatch.label.InventoryPreviewFixMode.wutong"),
    CUSTOMIZATION  ("customization", "inventorypreviewpatch.label.InventoryPreviewFixMode.customize"),
    ;

    private final String configString;
    private final String unlocName;

    InventoryPreviewFixMode(String configString, String unlocName) {
        this.configString = configString;
        this.unlocName = unlocName;
    }

    public static InventoryPreviewFixMode fromStringStatic(String name) {
        for (InventoryPreviewFixMode mode : InventoryPreviewFixMode.values()) {
            if (mode.configString.equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return InventoryPreviewFixMode.NO;
    }

    @Override
    public String getStringValue() {
        return this.configString;
    }

    @Override
    public String getDisplayName() {
        return StringUtils.translate(this.unlocName);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        int id = this.ordinal();
        if (forward) {
            if (id == 1 && !isLoadedWuTongUI()) {
                id++;
            }
            if (++id >= values().length) {
                id = 0;
            }
        } else {
            if (id == 1 && !isLoadedWuTongUI()) {
                id--;
            }
            if (--id < 0) {
                id = values().length - 1;
            }
        }
        return values()[id % values().length];
    }

    @Override
    public InventoryPreviewFixMode fromString(String name) {
        return fromStringStatic(name);
    }
}