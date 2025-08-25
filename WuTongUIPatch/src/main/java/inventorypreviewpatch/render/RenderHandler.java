package inventorypreviewpatch.render;

import fi.dy.masa.malilib.util.GuiUtils;
import inventorypreviewpatch.ModUtils;
import inventorypreviewpatch.event.HitListener;
import inventorypreviewpatch.helper.MethodExecuteHelper;
import inventorypreviewpatch.interfaces.ModIRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;

public class RenderHandler implements ModIRenderer {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onRenderBeforeScreenOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        //修改容器标题
        Screen currentscreen = mc.currentScreen;
        if (!ModUtils.isGenericScreen(currentscreen)) {
            return;
        }
        BlockEntity blockEntity = HitListener.getInstance().blockEntity;
        Entity entity = HitListener.getInstance().entity;
        var containerEntity = blockEntity != null ?  blockEntity : entity;
        WuTongUIOverlayHandler.setTitle(currentscreen, containerEntity);
        //加入按钮组件（小抄）
        CheatSheetOverlay.getInstance(currentscreen).addCheatSheetButton();
    }

    @Override
    public void onRenderScreenOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        Screen currentscreen = mc.currentScreen;
        if (!ModUtils.isGenericScreen(currentscreen)) {
            return;
        }
        int x = GuiUtils.getScaledWindowWidth() / 2;
        int y = GuiUtils.getScaledWindowHeight() / 2;
        BlockEntity blockEntity = HitListener.getInstance().blockEntity;
        Entity entity = HitListener.getInstance().entity;
        var containerEntity = blockEntity != null ?  blockEntity : entity;
        WuTongUIOverlayHandler.drawTitle(drawContext, currentscreen, containerEntity);
        //绘制按钮组件（小抄）
        CheatSheetOverlay.getInstance(currentscreen).renderButton(drawContext, x, y);
        CheatSheetOverlay.getInstance(currentscreen).renderCheatSheet(drawContext, x, y);
        CreeperForewarnOverlay.renderCreeperForewarn(currentscreen, drawContext, x, y, mc);
    }

    @Override
    public void onRenderGameOverlayPostAdvanced(DrawContext drawContext, float partialTicks, Profiler profiler, MinecraftClient mc) {

        int x = GuiUtils.getScaledWindowWidth() / 2;
        int y = GuiUtils.getScaledWindowHeight() / 2;
        Screen currentscreen = mc.currentScreen;
        //监测是否在预览状态
        MethodExecuteHelper.updateCounter("inventory_preview");
        //负责写入
        CreeperForewarnOverlay.updateState(mc, partialTicks);
        CreeperForewarnOverlay.renderCreeperForewarn(currentscreen, drawContext, x, y, mc);
    }
}