package inventorypreviewpatch;

import fi.dy.masa.malilib.event.RenderEventHandler;
import inventorypreviewpatch.configs.Configs;
import inventorypreviewpatch.event.HitListener;
import inventorypreviewpatch.event.ModRenderEventHandler;
import inventorypreviewpatch.event.ResourcesLoadedListener;
import inventorypreviewpatch.impl.TickExecutorImpl;
import inventorypreviewpatch.interfaces.IRenderers;
import inventorypreviewpatch.interfaces.ITickExecutor;
import inventorypreviewpatch.render.RenderHandler;

public class InitHandler {
    private static final InitHandler INSTANCE = new InitHandler();

    public static InitHandler getInstance() {
        return INSTANCE;
    }

    public void registerModHandlers() {
        Configs.ConfigsRegister();

        HitListener.getInstance().upDateHitResult();
        ModRenderEventHandler.registerRenderers();
        ResourcesLoadedListener.getInstance().UpdateState();

        IRenderers modRenderer = new RenderHandler();
        RenderEventHandler.getInstance().registerGameOverlayRenderer(modRenderer);
        ModRenderEventHandler.getInstance().registerRenderer(modRenderer);

        ITickExecutor executor = new TickExecutorImpl();
        ModRenderEventHandler.getInstance().registerTickExecutor(executor);
    }
}
