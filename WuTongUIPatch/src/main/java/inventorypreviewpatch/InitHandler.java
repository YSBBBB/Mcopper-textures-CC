package inventorypreviewpatch;

import fi.dy.masa.malilib.event.RenderEventHandler;
import inventorypreviewpatch.configs.Configs;
import inventorypreviewpatch.event.HitListener;
import inventorypreviewpatch.event.ModRenderEventHandler;
import inventorypreviewpatch.event.ResourcesLoadedListener;
import inventorypreviewpatch.interfaces.ModIRenderer;
import inventorypreviewpatch.render.RenderHandler;

public class InitHandler
{
    private static final InitHandler INSTANCE = new InitHandler();
    public static InitHandler getInstance() {return INSTANCE;}

    public void registerModHandlers()
    {
        Configs.ConfigsRegister();

        HitListener.getInstance().getHitBlockResult();
        HitListener.getInstance().getHitEntityResult();
        ModRenderEventHandler.registerRenderers();
        ResourcesLoadedListener.getInstance().setValue();

        ModIRenderer modRenderer = new RenderHandler();
        RenderEventHandler.getInstance().registerGameOverlayRenderer(modRenderer);
        ModRenderEventHandler.getInstance().registerRendererAfterScreenOverlay(modRenderer);
        ModRenderEventHandler.getInstance().registerRendererBeforeScreenOverlay(modRenderer);

    }
}
