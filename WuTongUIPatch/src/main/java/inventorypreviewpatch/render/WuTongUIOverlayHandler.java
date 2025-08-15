package inventorypreviewpatch.render;

import net.minecraft.block.entity.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import static inventorypreviewpatch.ModUtils.isChinese;
import static inventorypreviewpatch.ModUtils.isResourcePackLoaded;
import static inventorypreviewpatch.configs.Configs.Fixes.BARREL_FIXES;
import static inventorypreviewpatch.configs.Configs.Generic.Display_Container_Title_Mode;
import static inventorypreviewpatch.configs.Configs.Generic.Display_PlayInventory_Title_Mode;
import static net.minecraft.screen.ScreenHandlerType.GENERIC_9X6;

public class WuTongUIOverlayHandler {

    public WuTongUIOverlayHandler() {
    }

    public static void drawTitle(DrawContext drawContext, Screen screen, BlockEntity containerEntity) {
        //GenericContainerScree
        if (!isResourcePackLoaded()) return;
        if (!(screen instanceof GenericContainerScreen screen1)) return;
        int rows = screen1.getScreenHandler().getRows();
        int backgroundHeight = 114 + rows * 18;
        int titleX = (screen.getNavigationFocus().width() - 176) / 2 + 8;
        if (Display_PlayInventory_Title_Mode.getStringValue().equals("all")) {
            int playerInventoryTitleY = (screen.getNavigationFocus().height() - (backgroundHeight)) / 2 + backgroundHeight - 94;
            Text playerInventoryTitle = Text.translatable("key.categories.inventory");
            drawContext.drawText(screen.getTextRenderer(), playerInventoryTitle, titleX, playerInventoryTitleY, 4210752, false);
        }

        if (!(containerEntity instanceof BarrelBlockEntity || containerEntity instanceof EnderChestBlockEntity)) return;
        Text title;
        int titleY = (screen.getNavigationFocus().height() - (backgroundHeight)) / 2 + 6;
        if (containerEntity instanceof BarrelBlockEntity) {
            title = isChinese() && screen.getTitle().equals(Text.translatable("container.barrel")) ?
                    Display_Container_Title_Mode.getStringValue().equals("all") ? Text.of("木桶") : screen.getTitle() : screen.getTitle();
        } else {
            title = isChinese() ? Display_Container_Title_Mode.getStringValue().equals("all") ? Text.of("末影箱") : screen.getTitle() : screen.getTitle();
        }
        drawContext.drawText(screen.getTextRenderer(), title, titleX, titleY, 4210752, false);
    }

    public static void setTitle(Screen screen, BlockEntity containerEntity) {
        ScreenHandlerType<?> type = null;
        if (screen instanceof HandledScreen<?> handledScreen) {
            type = handledScreen.getScreenHandler().type;
            //强制显示物品栏标题功能开启后会在drawTitle()方法重新绘制新标题
            if (Display_PlayInventory_Title_Mode.getStringValue().equals("all") || Display_PlayInventory_Title_Mode.getStringValue().equals("no")) {
                handledScreen.playerInventoryTitle = Text.translatable("blank.title");
            }
        }

        //暂时割弃
       /* if (!isChinese() && containerEntity instanceof EnderChestBlockEntity && screen instanceof GenericContainerScreen) {
            //保证末影箱的GUI正常加载
            // \uB111也就是넑
            screen.title = Text.literal("\uB111");
        }*/

        //不显示标题功能
        if (Display_Container_Title_Mode.getStringValue().equals("no")) {
            if (isResourcePackLoaded()) {
                //不修改末影箱和酿造台的标题，防止GUI丢失
                if (screen.getTitle().equals(Text.translatable("container.enderchest")) || screen.getTitle().equals(Text.translatable("container.brewing")))
                    return;
            }
            //用 访问加宽器 修改title（这个版本我找不到setTitle()方法）
            screen.title = Text.translatable("blank.title");
        } else if (screen.getTitle().equals(Text.translatable("container.barrel"))) {
            //如果是大木桶的GUI，则把标题从“木桶”改为“大木桶”（阻止叠加错乱gui）
            if (type == GENERIC_9X6) {
                screen.title = Text.translatable("container.large.barrel");
            }
        }
    }

    public static void renderContainerGUI(Screen screen, BlockEntity containerEntity) {
        if (isResourcePackLoaded() && (BARREL_FIXES.getBooleanValue()) && screen instanceof GenericContainerScreen genericContainerScreen) {
           if (containerEntity instanceof BarrelBlockEntity) {
               //使用 访问加宽器 获取type，因为getType()方法在某些情况下会抛出UnsupportedOperationException错误，导致崩溃
               // 吐槽：返回null不好吗，直接抛出是什么非阳间做法
               // type = handledScreen.getScreenHandler().getType();
               ScreenHandlerType<?> type = genericContainerScreen.getScreenHandler().type;
               WuTongUIOverlay.renderBarrelOverlay(type);
           }
        }
    }

    public static void RenderFurnaceProcessingProgress(AbstractFurnaceBlockEntity furnace, int x, int y) {

        Identifier TEXTURE_FURNACE = Identifier.ofVanilla("textures/gui/container/furnace.png");
        if (furnace instanceof SmokerBlockEntity)
            TEXTURE_FURNACE = Identifier.ofVanilla("textures/gui/container/smoker.png");
        else if (furnace instanceof BlastFurnaceBlockEntity)
            TEXTURE_FURNACE = Identifier.ofVanilla("textures/gui/container/blast_furnace.png");

        boolean isBurning = furnace.propertyDelegate.get(0) > 0;
        int i = furnace.propertyDelegate.get(2);
        int j = furnace.propertyDelegate.get(3);
        int k = furnace.propertyDelegate.get(1);
        if (k == 0) {
            k = 200;
        }

        float cookProgress = j != 0 && i != 0 ? MathHelper.clamp((float) i / j, 0.0F, 1.0F) : 0.0f;
        float fuelProgress = MathHelper.clamp((float) furnace.propertyDelegate.get(0) / k, 0.0F, 1.0F);
        WuTongUIOverlay.renderFurnaceProgress(TEXTURE_FURNACE, x, y, cookProgress, fuelProgress, isBurning);
    }

    public static void RenderBrewingStandProcessingProgress(BrewingStandBlockEntity brewingStand, int x, int y) {
        final Identifier TEXTURE_BREWINGSTAND = Identifier.ofVanilla("textures/gui/container/brewing_stand.png");
        int brewTime = brewingStand.propertyDelegate.get(0);
        int fuel  = brewingStand.propertyDelegate.get(1);
        WuTongUIOverlay.renderBrewingStandProgress(TEXTURE_BREWINGSTAND, x, y, brewTime, fuel);
    }
}