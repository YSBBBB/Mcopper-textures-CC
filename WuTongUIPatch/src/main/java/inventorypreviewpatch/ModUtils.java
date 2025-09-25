package inventorypreviewpatch;

import inventorypreviewpatch.helper.MethodExecuteHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.BufferBuilder;

public class ModUtils {

    public static Object getFirstNonNull(Object... params) {
        for (Object param : params) {
            if (param != null) return param;
        }
        return null;
    }

    public static boolean isOnPreview(MinecraftClient mc) {
        //根据不同的帧数区间来设置超时时间，防止渣机错认为依然处在预览状态
        int timeoutMS = mc.getCurrentFps() <= 20 ? 250 : 75;
        return MethodExecuteHelper.getExecutionState("inventory_preview", timeoutMS);
    }

    public static boolean isContainerScreen(Screen screen) {
        boolean isContainerScreen = false;
        if (screen instanceof HandledScreen<?>) {
            isContainerScreen = !(screen instanceof CreativeInventoryScreen) && !(screen instanceof InventoryScreen);
        }
        return isContainerScreen;
    }

    //渲染
    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, float pixelWidth, BufferBuilder buffer) {
        drawTexturedRectBatched(x, y, u, v, width, height, 0, pixelWidth, buffer);
    }

    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, float zLevel, float pixelWidth, BufferBuilder buffer) {
        //pixelWidth即像素宽度应为1.0与材质文件边长的比值
        if (pixelWidth == 0) pixelWidth = 0.00390625F;
        buffer.vertex(x, y + height, zLevel).texture(u * pixelWidth, (v + height) * pixelWidth);
        buffer.vertex(x + width, y + height, zLevel).texture((u + width) * pixelWidth, (v + height) * pixelWidth);
        buffer.vertex(x + width, y, zLevel).texture((u + width) * pixelWidth, v * pixelWidth);
        buffer.vertex(x, y, zLevel).texture(u * pixelWidth, v * pixelWidth);
    }
}
