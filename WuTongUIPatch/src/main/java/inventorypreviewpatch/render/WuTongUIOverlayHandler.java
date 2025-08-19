package inventorypreviewpatch.render;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;

import static inventorypreviewpatch.ModUtils.isChinese;
import static inventorypreviewpatch.ModUtils.isLoadedWuTongUI;
import static inventorypreviewpatch.configs.Configs.Generic.*;
import static inventorypreviewpatch.configs.Configs.Fixes.*;
import static inventorypreviewpatch.render.WuTongUIOverlay.*;
import static net.minecraft.screen.ScreenHandlerType.GENERIC_9X3;
import static net.minecraft.screen.ScreenHandlerType.GENERIC_9X6;

public class WuTongUIOverlayHandler {

    public static void renderFrame(InventoryOverlay.InventoryRenderType type, BlockEntity be, int x, int y, int slotsPerRow, int totalSlots, int form) {
        RenderUtils.setupBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        BuiltBuffer builtBuffer;

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);

        switch (form) {
            case 1 -> renderInventoryBackground(type, x, y, slotsPerRow, totalSlots, buffer);
            case 2 -> renderSpecialInventoryBackground(be, x, y, totalSlots, buffer);
            case 3 -> renderBarrelBackground(x, y, totalSlots, buffer);
            case 4 -> renderFurnaceProgress((AbstractFurnaceBlockEntity) be, x, y, buffer);
            case 5 -> renderBrewingStandProgress((BrewingStandBlockEntity) be, x, y, buffer);
        }

        RenderSystem.enableBlend();

        try {
            builtBuffer = buffer.end();
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
            builtBuffer.close();
        } catch (Exception ignored) {
        }
    }

    public static void drawTitle(DrawContext drawContext, Screen screen, BlockEntity containerEntity, MinecraftClient mc) {

        if (!isLoadedWuTongUI()) return;
        if (!(screen instanceof GenericContainerScreen screen1)) return;
        int rows = screen1.getScreenHandler().getRows();
        int backgroundHeight = 114 + rows * 18;
        int titleX = (screen.getNavigationFocus().width() - 176) / 2 + 8;
        //物品栏标题
        if (Display_PlayInventory_Title_Mode.getStringValue().equals("all")) {
            int playerInventoryTitleY = (screen.getNavigationFocus().height() - (backgroundHeight)) / 2 + backgroundHeight - 94;
            Text playerInventoryTitle = Text.translatable("key.categories.inventory");
            drawContext.drawText(screen.getTextRenderer(), playerInventoryTitle, titleX, playerInventoryTitleY, 4210752, false);
        }

        //容器
        if (Display_Container_Title_Mode.getStringValue().equals("all")) {
            Text title = null;
            if (containerEntity instanceof Nameable nameableContainer) {
                //(这个title是容器名称)
                title = nameableContainer.getCustomName();
            }
            if (title == null) {
                if (containerEntity instanceof BarrelBlockEntity) {
                    if (isChinese()) {
                        title = screen1.getScreenHandler().type == GENERIC_9X6 ? Text.of("大木桶") : Text.of("木桶");
                    } else {
                        title = Text.translatable("container.barrel");
                    }
                } else if (containerEntity instanceof EnchantingTableBlockEntity) {
                    //
                    title = isChinese() ? Text.of("末影箱") : Text.translatable("container.enderchest");
                } else {
                    if (mc.world != null) {
                        Block block = mc.world.getBlockState(containerEntity.getPos()).getBlock();
                        title = block.getName();
                    }
                }
            }

            int titleY = (screen.getNavigationFocus().height() - (backgroundHeight)) / 2 + 6;
            drawContext.drawText(screen.getTextRenderer(), title, titleX, titleY, 4210752, false);
        }
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

        if (isLoadedWuTongUI()) {
            if (screen instanceof BrewingStandScreen && isChinese()) {
                //酿造台的小抄之后会将单独渲染（原版有点小bug）
                screen.title = Text.translatable("blank.title");
                //已确定当前屏幕为酿造台屏幕，不需要执行以下逻辑了
                return;
            } else if (screen instanceof GenericContainerScreen) {
                if (containerEntity instanceof BarrelBlockEntity) {
                    //让不同语言都能使用(大)木桶和末影箱的GUI
                    if (type == GENERIC_9X6 && BARREL_FIXES.getBooleanValue()) {
                        screen.title = Text.of("\ub110§f\ub119");
                    } else if (type == GENERIC_9X3) {
                        screen.title = Text.of("\ub110§f\ub116");
                    }
                    return;
                } else if (containerEntity instanceof EnderChestBlockEntity) {
                    screen.title = Text.of("\ub110§f\ub111");
                    return;
                }
             }
        }

        if (Display_Container_Title_Mode.getStringValue().equals("no") || Display_Container_Title_Mode.getStringValue().equals("all")) {
            //用 访问加宽器 修改title（这个版本我找不到setTitle()方法
            //如果是all，会在之后重新绘制标题文本
            screen.title = Text.translatable("blank.title");
        }
    }
}