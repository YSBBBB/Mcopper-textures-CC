package inventorypreviewpatch.render;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.block.entity.*;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.render.*;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;

import static inventorypreviewpatch.configs.Configs.Fixes.BARREL_FIXES;
import static inventorypreviewpatch.configs.Configs.Generic.Display_Container_Title_Mode;
import static inventorypreviewpatch.configs.Configs.Generic.Display_PlayInventory_Title_Mode;
import static inventorypreviewpatch.event.ResourcesLoadedListener.*;
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

    public static <T> void drawTitle(DrawContext drawContext, Screen screen, T containerEntity) {

        if (!isLoadedWuTongUI) return;
        int color = 4210752;
        TextRenderer textRenderer = screen.getTextRenderer();
        int rows = screen instanceof GenericContainerScreen screen2 ? screen2.getScreenHandler().getRows() : 0;
        int backgroundHeight = 114 + rows * 18;
        int titleX = (screen.getNavigationFocus().width() - 176) / 2 + 8;
        //物品栏标题
        if (Display_PlayInventory_Title_Mode.getStringValue().equals("all")) {
            int playerInventoryTitleY = (screen.getNavigationFocus().height() - (backgroundHeight)) / 2 + backgroundHeight - 94;
            Text playerInventoryTitle = Text.translatable("key.categories.inventory");
            drawContext.drawText(textRenderer, playerInventoryTitle, titleX, playerInventoryTitleY, color, false);
        }

        //容器
        if (Display_Container_Title_Mode.getStringValue().equals("all")) {
            Text title = null;
            //如果被改过名，就用改后的名字
            if (containerEntity instanceof Nameable nameableContainer) {
                title = nameableContainer.getCustomName();
            }
            if (title == null) {
                title = amendTitle(screen, containerEntity);
            }
            int titleY = (screen.getNavigationFocus().height() - (backgroundHeight)) / 2 + 6;
            if (!(screen instanceof GenericContainerScreen)) {
                titleX = (screen.getNavigationFocus().width() - textRenderer.getWidth(title.asOrderedText())) / 2;
                titleY = titleY - (screen instanceof HopperScreen ? 25 : 43);
                color = 0xFF606060;
            }
            drawContext.drawText(textRenderer, title, titleX, titleY, color, false);
        }

    }

    public static <T> void setTitle(Screen screen, T containerEntity) {
        ScreenHandlerType<?> type;
        if (screen instanceof HandledScreen<?> handledScreen) {
            type = handledScreen.getScreenHandler().type;
            //强制显示物品栏标题功能开启后会在drawTitle()方法重新绘制新标题
            if (Display_PlayInventory_Title_Mode.getStringValue().equals("all") || Display_PlayInventory_Title_Mode.getStringValue().equals("no")) {
                handledScreen.playerInventoryTitle = Text.translatable("inventorypreviewpatch.blank.title");
            }

            if (isLoadedWuTongUI) {
                if (screen instanceof BrewingStandScreen && (isChinese || isEN_US)) {
                    //酿造台的小抄之后会将单独渲染（原版有点小bug）
                    screen.title = Text.translatable("inventorypreviewpatch.blank.title");
                    return;
                } else if (containerEntity instanceof BarrelBlockEntity) {
                    //让不同语言都能使用(大)木桶和末影箱的GUI
                    if (type == GENERIC_9X6 && BARREL_FIXES.getBooleanValue()) {
                        screen.title = Text.translatable("inventorypreviewpatch.large.barrel.title");
                    } else if (type == GENERIC_9X3) {
                        screen.title = Text.translatable("inventorypreviewpatch.barrel.title");
                    }
                    return;
                } else if (containerEntity instanceof EnderChestBlockEntity) {
                    screen.title = Text.translatable("inventorypreviewpatch.enderchest.title");
                    return;
                } else if (containerEntity instanceof AbstractMinecartEntity) {
                    screen.title = Text.translatable("inventorypreviewpatch.minecart.title");
                    return;
                } else if (containerEntity instanceof AbstractBoatEntity) {
                    screen.title = Text.translatable("inventorypreviewpatch.boat.title");
                    return;
                } else if (containerEntity instanceof TrappedChestBlockEntity) {
                    screen.title = amendTitle(screen, containerEntity);
                    return;
                }
            }

            if (Display_Container_Title_Mode.getStringValue().equals("no")) {
                //用 访问加宽器 修改title（这个版本我找不到setTitle()方法
                screen.title = Text.translatable("inventorypreviewpatch.blank.title");
            }
        }
    }

}