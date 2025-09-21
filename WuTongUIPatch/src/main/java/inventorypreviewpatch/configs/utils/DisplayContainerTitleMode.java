package inventorypreviewpatch.configs.utils;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum DisplayContainerTitleMode implements IConfigOptionListEntry {
    ALL      ("all", "inventorypreviewpatch.label.DisplayContainerTitleMode.all"),
    VANILLA  ("vanilla", "inventorypreviewpatch.label.DisplayContainerTitleMode.vanilla"),
    NO       ("no", "inventorypreviewpatch.label.DisplayContainerTitleMode.no"),
    ;

    private final String configString;
    private final String unlocName;

    DisplayContainerTitleMode(String configString, String unlocName) {
        this.configString = configString;
        this.unlocName = unlocName;
    }

    public static DisplayContainerTitleMode fromStringStatic(String name) {
        for (DisplayContainerTitleMode mode : DisplayContainerTitleMode.values()) {
            if (mode.configString.equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return DisplayContainerTitleMode.VANILLA;
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
    public DisplayContainerTitleMode fromString(String name) {
        return fromStringStatic(name);
    }
}
