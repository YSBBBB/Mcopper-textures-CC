package inventorypreviewpatch.interfaces;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public interface ModIRenderer
{
    /**
     * 在原版Screen完成渲染后调用
     * 注册方法为{@link inventorypreviewpatch.event.ModEventHandler#registerRendererAfterScreenOverlay(ModIRenderer)}
     * @param drawContext
     * @param mouseX
     * @param mouseY
     * @param tickDelta
     */

    default void onRenderScreenOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {}

    /**
     * 在原版Screen完成渲染前调用
     * 注册方法为{@link inventorypreviewpatch.event.ModEventHandler#registerRendererBeforeScreenOverlay(ModIRenderer)}
     * @param drawContext
     * @param mouseX
     * @param mouseY
     * @param tickDelta
     */

    default void onRenderBeforeScreenOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {}

    default void onRenderBeforeTick(Screen screen) {}

    default void onRenderAfterTick(Screen screen) {};

    default void onRenderAfterTooltip() {};

    default void onRenderBeforeTooltip() {};

}

