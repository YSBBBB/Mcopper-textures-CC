package inventorypreviewpatch.interfaces;

import inventorypreviewpatch.event.ModRenderEventHandler;
import net.minecraft.client.gui.DrawContext;

public interface ModIRenderer
{
    /**
     * 在原版Screen完成渲染后调用
     * 注册方法为{@link ModRenderEventHandler#registerRendererAfterScreenOverlay(ModIRenderer)}
     * @param drawContext
     * @param mouseX
     * @param mouseY
     * @param tickDelta
     */

    default void onRenderScreenOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {}

    /**
     * 在原版Screen完成渲染前调用
     * 注册方法为{@link ModRenderEventHandler#registerRendererBeforeScreenOverlay(ModIRenderer)}
     * @param drawContext
     * @param mouseX
     * @param mouseY
     * @param tickDelta
     */

    default void onRenderBeforeScreenOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {}
}

