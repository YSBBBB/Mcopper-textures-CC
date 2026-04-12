package inventorypreviewpatch.interfaces;

import fi.dy.masa.malilib.interfaces.IRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public interface IRenderers extends IRenderer {
    /**
     * 在原版Screen完成渲染后调用
     * @param drawContext ()
     * @param mouseX ()
     * @param mouseY ()
     * @param tickDelta ()
     */
    default void onRenderScreenOverlay(MinecraftClient client, DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int scaledWidth, int scaledHeight) {
    }

    /**
     * 在原版Screen完成渲染前调用(init()被调用前)
     * @param client ()
     * @param screen ()
     */
    default void beforeInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
    }

    /**
     * 在原版Screen完成渲染前调用(init()被调用后)
     * @param client ()
     * @param screen ()
     */
    default void AfterInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
    }
}

