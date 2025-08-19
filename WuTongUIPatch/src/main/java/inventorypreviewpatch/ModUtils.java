package inventorypreviewpatch;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class ModUtils {
    public static boolean isLoadedWuTongUI() {
        ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
        final Identifier identifier = Identifier.ofVanilla("textures/font/b116.png");
        return manager.getResource(identifier).isPresent();
    }

    //没被使用
    /*public static boolean isLoadedMcopper() {
        ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
        final Identifier identifier = Identifier.ofVanilla("textures/font/b100.png");
        return manager.getResource(identifier).isPresent();
    }*/

    public static boolean isChinese() {
        String language = MinecraftClient.getInstance().getLanguageManager().getLanguage();
        return (language.equals("zh_cn")) || (language.equals("zh_hk")) || (language.equals("zh_tw"));
    }

    //render

    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, float pixelWidth ,BufferBuilder buffer)
    {
        drawTexturedRectBatched(x, y, u, v, width, height, 0, pixelWidth,buffer);
    }

    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, float zLevel, float pixelWidth, BufferBuilder buffer)
    {
        //pixelWidth即像素宽度可以是1.0与材质文件边长的比值
        if (pixelWidth == 0 ) pixelWidth =0.00390625F;

        buffer.vertex(x           , y + height, zLevel).texture( u          * pixelWidth, (v + height) * pixelWidth);
        buffer.vertex(x + width, y + height, zLevel).texture((u + width) * pixelWidth, (v + height) * pixelWidth);
        buffer.vertex(x + width, y            , zLevel).texture((u + width) * pixelWidth,  v           * pixelWidth);
        buffer.vertex(x           , y            , zLevel).texture( u          * pixelWidth,  v           * pixelWidth);
    }

}
