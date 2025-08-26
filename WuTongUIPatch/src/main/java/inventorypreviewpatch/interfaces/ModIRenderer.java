package inventorypreviewpatch.interfaces;

import fi.dy.masa.malilib.interfaces.IRenderer;
import inventorypreviewpatch.event.ModRenderEventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public interface ModIRenderer extends IRenderer {
    /**
     * 在原版Screen完成渲染后调用
     * 注册方法为{@link ModRenderEventHandler#registerRendererAfterScreenOverlay(ModIRenderer)}
     *
     * @param drawContext
     * @param mouseX
     * @param mouseY
     * @param tickDelta
     */
    default void onRenderScreenOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
    }

    /**
     * 在原版Screen完成渲染前调用
     * 注册方法为{@link ModRenderEventHandler#registerRendererBeforeScreenOverlay(ModIRenderer)}
     *
     * @param client
     * @param screen
     */
    default void onRenderBeforeScreenOverlay(MinecraftClient client, Screen screen) {
    }

}

