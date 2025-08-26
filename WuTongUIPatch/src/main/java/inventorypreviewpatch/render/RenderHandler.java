package inventorypreviewpatch.render;

import fi.dy.masa.malilib.util.GuiUtils;
import inventorypreviewpatch.ModUtils;
import inventorypreviewpatch.event.HitListener;
import inventorypreviewpatch.interfaces.ModIRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;

public class RenderHandler implements ModIRenderer {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onRenderBeforeScreenOverlay(MinecraftClient client, Screen screen) {
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
    public void onRenderScreenOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        Screen currentscreen = mc.currentScreen;
        int x = GuiUtils.getScaledWindowWidth() / 2;
        int y = GuiUtils.getScaledWindowHeight() / 2;
        if (ModUtils.isGenericScreen(currentscreen)) {
            BlockEntity blockEntity = HitListener.getInstance().blockEntity;
            Entity entity = HitListener.getInstance().entity;
            var containerEntity = blockEntity != null ? blockEntity : entity;
            WuTongUIOverlayHandler.drawTitle(drawContext, currentscreen, containerEntity);
            //绘制小抄
            CheatSheetOverlay.renderCheatSheet(drawContext, currentscreen, x, y);
        }
        if (currentscreen instanceof InventoryScreen || ModUtils.isGenericScreen(currentscreen)) {
            CreeperForewarnOverlay.renderCreeperForewarn(currentscreen, drawContext, x, y, mc);
        }
    }

    @Override
    public void onRenderGameOverlayPostAdvanced(DrawContext drawContext, float partialTicks, Profiler profiler, MinecraftClient mc) {
        int x = GuiUtils.getScaledWindowWidth() / 2;
        int y = GuiUtils.getScaledWindowHeight() / 2;
        Screen currentscreen = mc.currentScreen;
        //负责写入
        CreeperForewarnOverlay.updateState(mc, partialTicks);
        CreeperForewarnOverlay.renderCreeperForewarn(currentscreen, drawContext, x, y, mc);
    }
}