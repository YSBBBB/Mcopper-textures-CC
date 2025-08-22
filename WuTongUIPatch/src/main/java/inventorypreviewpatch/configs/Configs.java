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
import inventorypreviewpatch.configs.utils.*;
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

    public static class Generic {

        public static final ConfigBoolean DISPLAY_FURNACE_PROGRESS = new ConfigBoolean("DisplayFurnaceProgress", false, translate("DisplayFurnaceProgress")).apply(GENERIC_KEY);
        public static final ConfigBoolean DISPLAY_BREWING_STAND_PROGRESS = new ConfigBoolean("DisplayBrewingStandProgress", false, translate("DisplayBrewingStandProgress")).apply(GENERIC_KEY);
        public static final ConfigBoolean DISPLAY_TRAPPED_CHEST_TITLE = new ConfigBoolean("DisplayTrappedChestTitle", false, translate("DisplayTrappedChestTitle")).apply(GENERIC_KEY);
        public static final ConfigBoolean RENDER_LOCKED_HOPPER_MINECART = new ConfigBoolean("RenderLockedHopperMinecart", false, translate("RenderLockedHopperMinecart")).apply(GENERIC_KEY);
        public static final ConfigOptionList Display_Container_Title_Mode = new ConfigOptionList("DisplayContainerTitleMode", DisplayContainerTitleMode.VANILLA).apply(GENERIC_KEY);
        public static final ConfigOptionList Display_PlayInventory_Title_Mode = new ConfigOptionList("DisplayPlayInventoryTitleMode", DisplayPlayInventoryTitleMode.VANILLA).apply(GENERIC_KEY);
        public static final ConfigOptionList Render_Shulkerbox_Color_Mode = new ConfigOptionList("RenderShulkerboxColorMode", RenderShulkerboxColorMode.NO).apply(GENERIC_KEY);
        public static final ConfigOptionList Use_Cheat_Sheet = new ConfigOptionList("CheatSheetDisplayerMode", CheatSheetDisplayerMode.BrewingStand, translate("CheatSheetDisplayerMode")).apply(GENERIC_KEY);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                Use_Cheat_Sheet,
                DISPLAY_FURNACE_PROGRESS,
                DISPLAY_BREWING_STAND_PROGRESS,
                DISPLAY_TRAPPED_CHEST_TITLE,
                RENDER_LOCKED_HOPPER_MINECART,
                Display_Container_Title_Mode,
                Display_PlayInventory_Title_Mode,
                Render_Shulkerbox_Color_Mode
        );
    }

    public static class Fixes {
        public static final ConfigOptionList Inventory_Preview_Fix_Mode         = new ConfigOptionList("InventoryPreviewFixMode", InventoryPreviewFixMode.VANILLA).apply(Fixes_KEY);
        public static final ConfigBoolean BARREL_FIXES = new ConfigBoolean("BarrelFixes", true, translate("BarrelFixes")).apply(Fixes_KEY);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                Inventory_Preview_Fix_Mode,
                BARREL_FIXES
        );
    }

    public static void loadFromFile() {
        Path configFile = FileUtils.getConfigDirectoryAsPath().resolve(CONFIG_FILE_NAME);

        if (Files.exists(configFile) && Files.isReadable(configFile)) {
            JsonElement element = JsonUtils.parseJsonFileAsPath(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Generic", Configs.Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "Fixes", Configs.Fixes.OPTIONS);
            }
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

}