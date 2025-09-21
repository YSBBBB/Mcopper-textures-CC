package inventorypreviewpatch.configs.utils;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum DisplayPlayInventoryTitleMode implements IConfigOptionListEntry {
    ALL      ("all", "inventorypreviewpatch.label.DisplayPlayInventoryTitleMode.all"),
    VANILLA  ("vanilla", "inventorypreviewpatch.label.DisplayPlayInventoryTitleMode.vanilla"),
    NO       ("no", "inventorypreviewpatch.label.DisplayPlayInventoryTitleMode.no"),
    ;

    private final String configString;
    private final String unlocName;

    DisplayPlayInventoryTitleMode(String configString, String unlocName) {
        this.configString = configString;
        this.unlocName = unlocName;
    }

    public static DisplayPlayInventoryTitleMode fromStringStatic(String name) {
        for (DisplayPlayInventoryTitleMode mode : DisplayPlayInventoryTitleMode.values()) {
            if (mode.configString.equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return DisplayPlayInventoryTitleMode.VANILLA;
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
            if (++id >= values().length) {
                id = 0;
            }
        } else {
            if (--id < 0) {
                id = values().length - 1;
            }
        }
        return values()[id % values().length];
    }

    @Override
    public DisplayPlayInventoryTitleMode fromString(String name) {
        return fromStringStatic(name);
    }
}
