package inventorypreviewpatch.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import static inventorypreviewpatch.render.WuTongUIOverlay.PreviewOverlay.loadCustomMaterialParameters;

public class ResourcesLoadedListener {
    private static final ResourcesLoadedListener INSTANCE = new ResourcesLoadedListener();
    private static final Identifier Mcopper_ID = Identifier.ofVanilla("textures/font/b100.png");
    private static final Identifier WuTongUI_ID = net.minecraft.util.Identifier.ofVanilla("textures/font/b116.png");

    public static boolean isLoadedWuTongUI;
    public static boolean isLoadedMcopper;
    public static boolean isChinese;
    public static boolean isEN_US;

    public static ResourcesLoadedListener getInstance() {
        return INSTANCE;
    }

    public void UpdateState() {
        ClientLifecycleEvents.CLIENT_STARTED.register((minecraftClient -> {
            executor(minecraftClient);
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                    new SimpleSynchronousResourceReloadListener() {
                        @Override
                        public void reload(ResourceManager manager) {
                            executor(minecraftClient);
                        }

                        @Override
                        public Identifier getFabricId() {
                            return null;
                        }
                    }
            );
        }));
    }

    public static void executor(MinecraftClient mc) {
        String language = mc.getLanguageManager().getLanguage();
        isLoadedMcopper = mc.getResourceManager().getResource(Mcopper_ID).isPresent();
        isLoadedWuTongUI = mc.getResourceManager().getResource(WuTongUI_ID).isPresent();
        isChinese = (language.equals("zh_cn")) || (language.equals("zh_hk")) || (language.equals("zh_tw"));
        isEN_US = language.equals("en_us");
        loadCustomMaterialParameters(mc);
    }
}
