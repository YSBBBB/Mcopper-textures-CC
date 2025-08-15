package inventorypreviewpatch;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.data.ModInfo;
import inventorypreviewpatch.configs.Configs;
import inventorypreviewpatch.event.HitEventsHandler;
import inventorypreviewpatch.event.ModEventHandler;
import inventorypreviewpatch.gui.GuiConfigs;
import inventorypreviewpatch.interfaces.ModIRenderer;
import inventorypreviewpatch.render.RenderHandler;

public class InitHandler
{
    private static final InitHandler INSTANCE = new InitHandler();
    public static InitHandler getInstance() {return INSTANCE;}

    public void registerModHandlers()
    {
        ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID, new Configs());
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(
                new ModInfo(Reference.MOD_ID, Reference.MOD_NAME, GuiConfigs::new)
        );
        HitEventsHandler.getInstance().getHitResult();

        ModIRenderer modRenderer = new RenderHandler();
        ModEventHandler.getInstance().registerRendererAfterScreenOverlay(modRenderer);
        ModEventHandler.getInstance().registerRendererBeforeScreenOverlay(modRenderer);
    }
}
