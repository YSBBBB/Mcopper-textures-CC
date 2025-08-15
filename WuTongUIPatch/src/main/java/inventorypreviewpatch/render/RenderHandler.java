package inventorypreviewpatch.render;

import inventorypreviewpatch.event.HitEventsHandler;
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
        BlockEntity containerEntity = HitEventsHandler.getInstance().blockEntity;
        WuTongUIOverlayHandler.setTitle(currentscreen, containerEntity);
    }

    @Override
    public void onRenderScreenOverlay(DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        //绘制(大)木桶的GUI
        //LOGGER.info("2成功运行");
        MinecraftClient mc = MinecraftClient.getInstance();
        Screen currentscreen = mc.currentScreen;
        ResourceManager manager = mc.getResourceManager();
        if (mc.player == null || mc.world == null || currentscreen == null || manager == null) {
            return;
        }
        BlockEntity containerEntity = HitEventsHandler.getInstance().blockEntity;
        WuTongUIOverlayHandler.renderContainerGUI(currentscreen, containerEntity);
        WuTongUIOverlayHandler.drawTitle(drawContext, currentscreen, containerEntity);
    }
}