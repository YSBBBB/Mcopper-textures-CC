package inventorypreviewpatch.render;

import fi.dy.masa.malilib.util.GuiUtils;
import inventorypreviewpatch.ModUtils;
import inventorypreviewpatch.event.HitListener;
import inventorypreviewpatch.interfaces.IRenderers;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;

public class RenderHandler implements IRenderers {

    @Override
    public void onRenderBeforeScreenOverlay(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        Screen currentscreen = client.currentScreen;
        if (ModUtils.isGenericScreen(currentscreen)) {
            BlockEntity blockEntity = HitListener.getInstance().blockEntity;
            Entity entity = HitListener.getInstance().entity;
            var containerEntity = blockEntity != null ? blockEntity : entity;
            //修改容器标题
            WuTongUIOverlayHandler.setTitle(currentscreen, containerEntity);
        }
    }

    @Override
    public void onRenderScreenOverlay(MinecraftClient client, DrawContext drawContext, int mouseX, int mouseY, float tickDelta, int scaledWidth, int scaledHeight) {
        Screen currentscreen = client.currentScreen;
        int x = scaledWidth / 2;
        int y = scaledHeight / 2;
        if (ModUtils.isGenericScreen(currentscreen)) {
            BlockEntity blockEntity = HitListener.getInstance().blockEntity;
            Entity entity = HitListener.getInstance().entity;
            var containerEntity = blockEntity != null ? blockEntity : entity;
            assert currentscreen instanceof HandledScreen<?>;
            WuTongUIOverlayHandler.drawTitle(drawContext, (HandledScreen<?>) currentscreen, containerEntity);
            //绘制小抄
            CheatSheetOverlay.renderCheatSheet(drawContext, currentscreen, x, y);
        }
        if (currentscreen instanceof InventoryScreen || ModUtils.isGenericScreen(currentscreen)) {
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