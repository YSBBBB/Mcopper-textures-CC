package inventorypreviewpatch.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import inventorypreviewpatch.WuTongUIPatch;
import inventorypreviewpatch.configs.utils.InventoryPreviewFixMode;
import inventorypreviewpatch.render.CheatSheetOverlay;
import inventorypreviewpatch.render.WuTongUIOverlay;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.Reader;

import static inventorypreviewpatch.configs.Configs.Fixes.INVENTORY_PREVIEW_FIX_MODE;

public class ResourcesLoadedListener {
    private static final ResourcesLoadedListener INSTANCE = new ResourcesLoadedListener();
    private static final Identifier Mcopper_ID = Identifier.ofVanilla("textures/font/b100.png");
    private static final Identifier WuTongUI_ID = net.minecraft.util.Identifier.ofVanilla("textures/font/b116.png");
    private static final Identifier DATA_CORRECTION = Identifier.of("inventorypreviewpatch", "textures/overlay/data_correction.json");

    private static boolean isLoadedWuTongUI;
    private static boolean isLoadedMcopper;
    private static boolean isChinese;
    private static boolean isEN_US;

    public static ResourcesLoadedListener getInstance() {
        return INSTANCE;
    }

    public static boolean isLoadedMcopper() {
        return isLoadedMcopper;
    }
    public static boolean isLoadedWuTongUI() {
        return isLoadedWuTongUI;
    }
    public static boolean isChinese() {
        return isChinese;
    }
    public static boolean isEN_US() {
        return isEN_US;
    }

    private static void executeAtReload(MinecraftClient mc) {
        String language = mc.getLanguageManager().getLanguage();
        isLoadedMcopper = mc.getResourceManager().getResource(Mcopper_ID).isPresent();
        isLoadedWuTongUI = mc.getResourceManager().getResource(WuTongUI_ID).isPresent();
        isChinese = (language.equals("zh_cn")) || (language.equals("zh_hk")) || (language.equals("zh_tw"));
        isEN_US = language.equals("en_us");
        updateConfigs();
        loadCustomMaterialParameters(mc);
    }

    private static boolean isWuTongMode;
    private static void updateConfigs() {
        if (INVENTORY_PREVIEW_FIX_MODE.getStringValue().equals("wutong") && !isLoadedWuTongUI) {
            isWuTongMode = true;
            INVENTORY_PREVIEW_FIX_MODE.setOptionListValue(InventoryPreviewFixMode.VANILLA);
        } else if (isWuTongMode && isLoadedWuTongUI){
            isWuTongMode = false;
            INVENTORY_PREVIEW_FIX_MODE.setOptionListValue(InventoryPreviewFixMode.WUTONG);
        }
    }

    private static void loadCustomMaterialParameters(MinecraftClient mc) {
        if (mc.getResourceManager().getResource(DATA_CORRECTION).isEmpty()) {
            WuTongUIOverlay.PreviewOverlay.loadCustomMaterialParameters(null);
            CheatSheetOverlay.loadChestButtonParameters(null);
            return;
        }
        try {
            Reader reader = mc.getResourceManager().getResource(DATA_CORRECTION).get().getReader();
            JsonElement element = JsonParser.parseReader(reader);
            JsonObject object = element.getAsJsonObject();

            WuTongUIOverlay.PreviewOverlay.loadCustomMaterialParameters(object);
            CheatSheetOverlay.loadChestButtonParameters(object);
        } catch (IOException e) {
            WuTongUIPatch.LOGGER.error(String.valueOf(e));
        }
    }

    public void UpdateState() {
        ClientLifecycleEvents.CLIENT_STARTED.register((minecraftClient -> {
            executeAtReload(minecraftClient);
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                    new SimpleSynchronousResourceReloadListener() {
                        @Override
                        public void reload(ResourceManager manager) {
                            executeAtReload(minecraftClient);
                        }

                        @Override
                        public Identifier getFabricId() {
                            return null;
                        }
                    }
            );
        }));
    }
}
