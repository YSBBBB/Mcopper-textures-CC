package inventorypreviewpatch.event;

import inventorypreviewpatch.interfaces.IRenderers;
import inventorypreviewpatch.interfaces.ITickExecutor;
import inventorypreviewpatch.render.minecart.HopperMinecartRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import static net.minecraft.entity.EntityType.HOPPER_MINECART;


public class ModRenderEventHandler {

    private static final ModRenderEventHandler INSTANCE = new ModRenderEventHandler();

    public static ModRenderEventHandler getInstance() {
        return INSTANCE;
    }

    public void registerTickExecutor(ITickExecutor executor) {
        ClientTickEvents.START_CLIENT_TICK.register(client ->
            executor.executeOnTickStarted(client, client.getRenderTickCounter().getTickDelta(false))
        );
        ClientTickEvents.END_CLIENT_TICK.register(client ->
            executor.executeOnTickEnded(client, client.getRenderTickCounter().getTickDelta(false))
        );
    }

    public static void registerRenderers() {
        EntityRendererRegistry.register(HOPPER_MINECART, HopperMinecartRenderer::new);
    }

    public void registerRenderer(IRenderers renderer) {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                ScreenEvents.afterRender(screen).register((screen1, drawContext, mouseX, mouseY, tickDelta) ->
                        renderer.onRenderScreenOverlay(client, drawContext, mouseX, mouseY, tickDelta, scaledWidth, scaledHeight)
                ));
        ScreenEvents.BEFORE_INIT.register(renderer::beforeInit);
        ScreenEvents.AFTER_INIT.register(renderer::AfterInit);
    }
}