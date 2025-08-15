package inventorypreviewpatch.event;

import inventorypreviewpatch.interfaces.ModIRenderer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;


public class ModEventHandler {

    private static final ModEventHandler INSTANCE = new ModEventHandler();

    public static ModEventHandler getInstance() {
        return INSTANCE;
    }




    /*public void registerRendererWhenTitleRendered(ModIRenderer renderer) {
        final ThreadLocal<Boolean> IS_CALLED_BACKED_FROM_TITLE = new ThreadLocal<>();
        if (IS_CALLED_BACKED_FROM_TITLE.get() != null && IS_CALLED_BACKED_FROM_TITLE.get()) {
            return;
        }
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if () {

        }
    }*/

    /**
     * 为{@link ModIRenderer#onRenderScreenOverlay}方法注册一个渲染器
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
     * 为{@link ModIRenderer#onRenderBeforeScreenOverlay
     * ScreenOverlay}方法注册一个渲染器
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


    public void registerRendererBeforeTick(ModIRenderer renderer) {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                ScreenEvents.beforeTick(screen).register(renderer::onRenderBeforeTick));
    }

    public void registerRendererAfterTick(ModIRenderer renderer) {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                ScreenEvents.afterTick(screen).register(renderer::onRenderAfterTick));
    }

    public void registerRendererAfterTooltip(ModIRenderer renderer) {
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            renderer.onRenderAfterTooltip();
        });
    }

    public void registerRendererBeforeTooltip(ModIRenderer renderer) {
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            renderer.onRenderBeforeTooltip();
        });
    }


}