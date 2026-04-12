package inventorypreviewpatch.render;

import fi.dy.masa.malilib.util.GuiUtils;
import inventorypreviewpatch.ModUtils;
import inventorypreviewpatch.event.HitListener;
import inventorypreviewpatch.interfaces.IRenderers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public class RenderHandler implements IRenderers {

    @Override
    public void AfterInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        Screen currentscreen = client.currentScreen;
        if (ModUtils.isBEScreen(currentscreen, client.player)) {
            var containerEntity = ModUtils.getFirstNonNull(
                    HitListener.getInstance().getHitResult().inv(),
                    HitListener.getInstance().getHitResult().block()
            );
            WuTongUIOverlayHandler.setTitle(currentscreen, containerEntity);
            CheatSheetOverlay.addCheatSheetButton(screen);
        }
    }

    @Override
    public void onRenderScreenOverlay(MinecraftClient client, DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int scaledWidth, int scaledHeight) {
        Screen currentscreen = client.currentScreen;
        int x = scaledWidth / 2;
        int y = scaledHeight / 2;
        if (ModUtils.isBEScreen(currentscreen, client.player)) {
            CreeperForewarnOverlay.renderCreeperForewarn(currentscreen, drawContext, x, y, client);
            CheatSheetOverlay.renderCheatSheet(drawContext, currentscreen, x, y);
        } else if (currentscreen instanceof InventoryScreen) {
            CreeperForewarnOverlay.renderCreeperForewarn(currentscreen, drawContext, x, y, client);
        }
    }

    @Override
    public void onRenderGameOverlayPostAdvanced(DrawContext drawContext, float partialTicks, Profiler profiler, MinecraftClient client) {
        int x = GuiUtils.getScaledWindowWidth() / 2;
        int y = GuiUtils.getScaledWindowHeight() / 2;
        Screen currentscreen = client.currentScreen;
        CreeperForewarnOverlay.renderCreeperForewarn(currentscreen, drawContext, x, y, client);
    }
}