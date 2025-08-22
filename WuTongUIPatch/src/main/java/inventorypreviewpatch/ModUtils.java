package inventorypreviewpatch;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.BufferBuilder;

public class ModUtils {

    public static boolean isGenericScreen(Screen screen) {
        boolean isGenericScreen = false;
        if (screen instanceof HandledScreen<?>) {
            isGenericScreen = !(screen instanceof CreativeInventoryScreen) && !(screen instanceof InventoryScreen);
        }
        return isGenericScreen;
    }

    //render

    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, float pixelWidth, BufferBuilder buffer) {
        drawTexturedRectBatched(x, y, u, v, width, height, 0, pixelWidth, buffer);
    }

    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, float zLevel, float pixelWidth, BufferBuilder buffer) {
        //pixelWidth即像素宽度可以是1.0与材质文件边长的比值
        if (pixelWidth == 0) pixelWidth = 0.00390625F;

        buffer.vertex(x, y + height, zLevel).texture(u * pixelWidth, (v + height) * pixelWidth);
        buffer.vertex(x + width, y + height, zLevel).texture((u + width) * pixelWidth, (v + height) * pixelWidth);
        buffer.vertex(x + width, y, zLevel).texture((u + width) * pixelWidth, v * pixelWidth);
        buffer.vertex(x, y, zLevel).texture(u * pixelWidth, v * pixelWidth);
    }

}
