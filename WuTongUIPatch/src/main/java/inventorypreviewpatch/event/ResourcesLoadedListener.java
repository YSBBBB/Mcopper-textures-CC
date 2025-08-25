package inventorypreviewpatch.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class ResourcesLoadedListener {
    public static boolean isLoadedWuTongUI;
    public static boolean isLoadedMcopper;
    public static boolean isChinese;
    public static boolean isEN_US;

    private static final ResourcesLoadedListener INSTANCE = new ResourcesLoadedListener();
    public static ResourcesLoadedListener getInstance() {
        return INSTANCE;
    }

    public void setValue() {
        ClientLifecycleEvents.CLIENT_STARTED.register((minecraftClient -> {
            final Identifier Mcopper_ID = Identifier.ofVanilla("textures/font/b100.png");
            final Identifier WuTongUI_ID = Identifier.ofVanilla("textures/font/b116.png");
            String language = minecraftClient.getLanguageManager().getLanguage();
            isLoadedMcopper = minecraftClient.getResourceManager().getResource(Mcopper_ID).isPresent();
            isLoadedWuTongUI = minecraftClient.getResourceManager().getResource(WuTongUI_ID).isPresent();
            isChinese = (language.equals("zh_cn")) || (language.equals("zh_hk")) || (language.equals("zh_tw"));
            isEN_US = language.equals("en_us");
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                    new SimpleSynchronousResourceReloadListener() {
                        @Override
                        public void reload(ResourceManager manager) {
                            isLoadedMcopper = minecraftClient.getResourceManager().getResource(Mcopper_ID).isPresent();
                            String language = minecraftClient.getLanguageManager().getLanguage();
                            isLoadedWuTongUI = manager.getResource(WuTongUI_ID).isPresent();
                            isChinese = (language.equals("zh_cn")) || (language.equals("zh_hk")) || (language.equals("zh_tw"));
                            isEN_US = language.equals("en_us");
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
