package inventorypreviewpatch.render;

import fi.dy.masa.malilib.util.GuiUtils;
import inventorypreviewpatch.event.HitListener;
import inventorypreviewpatch.interfaces.ModIRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.ResourceManager;

public class RenderHandler implements ModIRenderer {
    public RenderHandler() {}

    @Override
    public void onRenderBeforeScreenOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        //修改容器标题
        MinecraftClient mc = MinecraftClient.getInstance();
        Screen currentscreen = mc.currentScreen;
        if (mc.player == null || mc.world == null || currentscreen == null) {
            return;
        }
        BlockEntity containerEntity = HitListener.getInstance().blockEntity;
        WuTongUIOverlayHandler.setTitle(currentscreen, containerEntity);
        //加入按钮组件（小抄）
        CheatSheetOverlay.getInstance(currentscreen).addCheatSheetButton();
    }

    @Override
    public void onRenderScreenOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ResourceManager manager = mc.getResourceManager();
        Screen currentscreen = mc.currentScreen;
        if (mc.player == null || mc.world == null || currentscreen == null || manager == null) {
            return;
        }
        int x = GuiUtils.getScaledWindowWidth() / 2;
        int y = GuiUtils.getScaledWindowHeight() / 2;
        BlockEntity containerEntity = HitListener.getInstance().blockEntity;
        WuTongUIOverlayHandler.drawTitle(drawContext, currentscreen, containerEntity, mc);
        //绘制按钮组件（小抄）
        CheatSheetOverlay.getInstance(currentscreen).renderButton(drawContext, x, y);
        CheatSheetOverlay.getInstance(currentscreen).renderCheatSheet(drawContext, x, y);
    }
}