package inventorypreviewpatch.event;

import inventorypreviewpatch.interfaces.ModIRenderer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;


public class ModRenderEventHandler {

    private static final ModRenderEventHandler INSTANCE = new ModRenderEventHandler();

    public static ModRenderEventHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 为{@link ModIRenderer#onRenderScreenOverlay}
     * 方法注册一个渲染器
     * 其在原版Screen渲染完成后调用
     *
     * @param renderer
     */

    public void registerRendererAfterScreenOverlay(ModIRenderer renderer) {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                ScreenEvents.afterRender(screen).register((screen1, drawContext, mouseX, mouseY, tickDelta) ->
                        renderer.onRenderScreenOverlay(drawContext, mouseX, mouseY, tickDelta)
                ));
    }

    /**
     * 为{@link ModIRenderer#onRenderBeforeScreenOverlay}
     * 方法注册一个渲染器
     * 其在原版Screen渲染完成后调用
     *
     * @param renderer
     */

    public void registerRendererBeforeScreenOverlay(ModIRenderer renderer) {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                ScreenEvents.beforeRender(screen).register((screen1, drawContext, mouseX, mouseY, tickDelta) ->
                        renderer.onRenderBeforeScreenOverlay(drawContext, mouseX, mouseY, tickDelta)
                ));
    }
}