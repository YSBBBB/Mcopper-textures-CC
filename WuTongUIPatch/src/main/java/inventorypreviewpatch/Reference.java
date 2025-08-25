package inventorypreviewpatch;

import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.MinecraftVersion;

public class Reference {
    public static final String MOD_ID = "inventorypreviewpatch";
    public static final String MOD_KEY = "inventory-preview-patch";
    public static final String MOD_NAME = "WuTongUI Patch";
    public static final String MOD_VERSION = StringUtils.getModVersionString(MOD_ID);
    public static final String MC_VERSION = MinecraftVersion.CURRENT.getName();
    public static final String MOD_STRING = MOD_ID + "-" + MC_VERSION + "-" + MOD_VERSION;
}
