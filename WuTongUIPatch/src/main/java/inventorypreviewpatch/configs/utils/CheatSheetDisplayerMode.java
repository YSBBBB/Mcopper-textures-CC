package inventorypreviewpatch.configs.utils;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

import static inventorypreviewpatch.event.ResourcesLoadedListener.isLoadedWuTongUI;

public enum CheatSheetDisplayerMode implements IConfigOptionListEntry {
    NO             ("no", "inventorypreviewpatch.label.CheatSheetDisplayerMode.no"),
    ALL            ("all", "inventorypreviewpatch.label.CheatSheetDisplayerMode.all"),
    BREWING_STAND  ("brewingStand", "inventorypreviewpatch.label.CheatSheetDisplayerMode.brewingStand"),
    CUSTOMIZATION  ("customization", "inventorypreviewpatch.label.CheatSheetDisplayerMode.customize"),
    ;

    private final String configString;
    private final String unlocName;

    CheatSheetDisplayerMode(String configString, String unlocName) {
        this.configString = configString;
        this.unlocName = unlocName;
    }

    public static CheatSheetDisplayerMode fromStringStatic(String name) {
        for (CheatSheetDisplayerMode mode : CheatSheetDisplayerMode.values()) {
            if (mode.configString.equalsIgnoreCase(name)) {
                return mode;
            }
        }

        return CheatSheetDisplayerMode.NO;
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
            if (id == 1 && !isLoadedWuTongUI) {
                id++;
            }
            if (++id >= values().length) {
                id = 0;
            }
        } else {
            if (id == 1 && !isLoadedWuTongUI) {
                id--;
            }
            if (--id < 0) {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public CheatSheetDisplayerMode fromString(String name) {
        return fromStringStatic(name);
    }
}
