package inventorypreviewpatch.configs.utils;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum RenderShulkerboxColorMode implements IConfigOptionListEntry {
    NO       ("no",     "inventorypreviewpatch.label.RenderShulkerboxColorMode.no")  ,
    HALF     ("half",   "inventorypreviewpatch.label.RenderShulkerboxColorMode.half"),
    ALL      ("all",    "inventorypreviewpatch.label.RenderShulkerboxColorMode.all") ,
    ;

    private final String configString;
    private final String unlocName;

    RenderShulkerboxColorMode(String configString, String unlocName)
    {
        this.configString = configString;
        this.unlocName = unlocName;
    }

    @Override
    public String getStringValue() {return this.configString;}

    @Override
    public String getDisplayName() {return StringUtils.translate(this.unlocName);}

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        int id = this.ordinal();

        if (forward)
        {
            if (++id >= values().length)
            {
                id = 0;
            }
        }
        else
        {
            if (--id < 0)
            {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public RenderShulkerboxColorMode fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static RenderShulkerboxColorMode fromStringStatic(String name)
    {
        for (RenderShulkerboxColorMode mode : RenderShulkerboxColorMode.values())
        {
            if (mode.configString.equalsIgnoreCase(name))
            {
                return mode;
            }
        }

        return RenderShulkerboxColorMode.NO;
    }

}
