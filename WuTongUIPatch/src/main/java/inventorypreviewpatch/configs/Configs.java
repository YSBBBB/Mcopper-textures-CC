package inventorypreviewpatch.configs;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.ModInfo;
import inventorypreviewpatch.Reference;
import inventorypreviewpatch.WuTongUIPatch;
import inventorypreviewpatch.configs.utils.CheatSheetDisplayerMode;
import inventorypreviewpatch.configs.utils.InventoryPreviewFixMode;
import inventorypreviewpatch.configs.utils.RenderShulkerboxColorMode;
import inventorypreviewpatch.gui.GuiConfigs;

import java.nio.file.Files;
import java.nio.file.Path;

import static net.minecraft.client.resource.language.I18n.translate;

public class Configs implements IConfigHandler {
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";
    private static final String GENERIC_KEY = Reference.MOD_ID + ".config.generic";
    private static final String Fixes_KEY = Reference.MOD_ID + ".config.fixes";

    public static void ConfigsRegister() {
        ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID, new Configs());
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(
                new ModInfo(Reference.MOD_ID, Reference.MOD_NAME, GuiConfigs::new)
        );
    }

    public static void loadFromFile() {
        Path configFile = FileUtils.getConfigDirectoryAsPath().resolve(CONFIG_FILE_NAME);
        if (Files.exists(configFile) && Files.isReadable(configFile)) {
            JsonElement element = JsonUtils.parseJsonFileAsPath(configFile);
            if (element == null || !element.isJsonObject()) return;
            JsonObject root = element.getAsJsonObject();
            ConfigUtils.readConfigBase(root, "Generic", Configs.Generic.OPTIONS);
            ConfigUtils.readConfigBase(root, "Fixes", Configs.Fixes.OPTIONS);
        } else {
            WuTongUIPatch.LOGGER.error("loadFromFile(): Failed to load config file '{}'.", configFile.toAbsolutePath());
        }
    }

    public static void saveToFile() {
        Path dir = FileUtils.getConfigDirectoryAsPath();
        if (!Files.exists(dir)) {
            FileUtils.createDirectoriesIfMissing(dir);
        }
        if (Files.isDirectory(dir)) {
            JsonObject root = new JsonObject();
            ConfigUtils.writeConfigBase(root, "Generic", Configs.Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Fixes", Configs.Fixes.OPTIONS);
            JsonUtils.writeJsonToFileAsPath(root, dir.resolve(CONFIG_FILE_NAME));
        } else {
            WuTongUIPatch.LOGGER.error("saveToFile(): Config Folder '{}' does not exist!", dir.toAbsolutePath());
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveToFile();
    }

    public static class Generic {

        public static final ConfigBoolean DISPLAY_FURNACE_PROGRESS = new ConfigBoolean("DisplayFurnaceProgress", false, translate("DisplayFurnaceProgress")).apply(GENERIC_KEY);
        public static final ConfigBoolean DISPLAY_BREWING_STAND_PROGRESS = new ConfigBoolean("DisplayBrewingStandProgress", false, translate("DisplayBrewingStandProgress")).apply(GENERIC_KEY);
        public static final ConfigBoolean DISPLAY_TRAPPED_CHEST_TITLE = new ConfigBoolean("DisplayTrappedChestTitle", false, translate("DisplayTrappedChestTitle")).apply(GENERIC_KEY);
        public static final ConfigBoolean RENDER_LOCKED_HOPPER_MINECART = new ConfigBoolean("RenderLockedHopperMinecart", false, translate("RenderLockedHopperMinecart")).apply(GENERIC_KEY);
        public static final ConfigBoolean CREEPER_FOREWARN = new ConfigBoolean("CreeperForewarn", false, translate("CreeperForewarn")).apply(GENERIC_KEY);
        public static final ConfigBoolean BLANK_TITLE = new ConfigBoolean("BlankTitle", false, translate("BlankTitle")).apply(GENERIC_KEY);
        public static final ConfigOptionList RENDER_SHULKERBOX_COLOR_MODE = new ConfigOptionList("RenderShulkerboxColorMode", RenderShulkerboxColorMode.NO).apply(GENERIC_KEY);
        public static final ConfigOptionList USE_CHEAT_SHEET = new ConfigOptionList("CheatSheetDisplayerMode", CheatSheetDisplayerMode.BREWING_STAND, translate("CheatSheetDisplayerMode")).apply(GENERIC_KEY);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                BLANK_TITLE,
                USE_CHEAT_SHEET,
                CREEPER_FOREWARN,
                DISPLAY_FURNACE_PROGRESS,
                DISPLAY_BREWING_STAND_PROGRESS,
                DISPLAY_TRAPPED_CHEST_TITLE,
                RENDER_LOCKED_HOPPER_MINECART,
                RENDER_SHULKERBOX_COLOR_MODE
        );
    }

    public static class Fixes {
        public static final ConfigOptionList INVENTORY_PREVIEW_FIX_MODE = new ConfigOptionList("InventoryPreviewFixMode", InventoryPreviewFixMode.NO).apply(Fixes_KEY);
        public static final ConfigBoolean BARREL_FIXES = new ConfigBoolean("BarrelFixes", false, translate("BarrelFixes")).apply(Fixes_KEY);
        public static final ConfigBoolean PREVENT_PREVIEWING_OWN_BACKPACK = new ConfigBoolean("PreventPreviewingOwnBackpack", false, translate("PreventPreviewingOwnBackpack")).apply(Fixes_KEY);
        public static final ConfigBoolean HORSE_FIXES = new ConfigBoolean("HorseFixes", false, translate("HorseFixes")).apply(Fixes_KEY);
        public static final ConfigBoolean JADE_TOOLTIP_FIXES = new ConfigBoolean("JadeTooltipFixes", false, translate("JadeTooltipFixes")).apply(Fixes_KEY);
        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                PREVENT_PREVIEWING_OWN_BACKPACK,
                INVENTORY_PREVIEW_FIX_MODE,
                BARREL_FIXES,
                HORSE_FIXES,
                JADE_TOOLTIP_FIXES
        );
    }
}