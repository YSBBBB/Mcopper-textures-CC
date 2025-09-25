package inventorypreviewpatch.render;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import inventorypreviewpatch.mixin.Accessors;
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
import static inventorypreviewpatch.configs.Configs.Generic.*;
import static inventorypreviewpatch.event.ResourcesLoadedListener.*;
import static inventorypreviewpatch.render.WuTongUIOverlay.PreviewOverlay.*;
import static inventorypreviewpatch.render.WuTongUIOverlay.amendTitle;
import static net.minecraft.screen.ScreenHandlerType.GENERIC_9X3;
import static net.minecraft.screen.ScreenHandlerType.GENERIC_9X6;

public class WuTongUIOverlayHandler {
    //一个渲染的框架
    public static void renderFrame(InventoryOverlay.InventoryRenderType type, InventoryOverlay.Context previewData, int x, int y, int slotsPerRow, int totalSlots, int form) {
        RenderUtils.setupBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        BuiltBuffer builtBuffer;

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
        switch (form) {
            case 1 -> renderWuTongInventoryBackground(type, previewData, x, y, slotsPerRow, totalSlots, buffer);
            case 2 -> renderWuTongBarrelBackground(x, y, totalSlots, buffer);
            case 3 -> renderFurnaceProgress((AbstractFurnaceBlockEntity) previewData.be(), x, y, buffer);
            case 4 -> renderBrewingStandProgress((BrewingStandBlockEntity) previewData.be(), x, y, buffer);
            case 5 -> renderVanillaInventoryBackground(type, x, y, slotsPerRow, totalSlots, buffer);
        }
        RenderSystem.enableBlend();

        try {
            builtBuffer = buffer.end();
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
            builtBuffer.close();
        } catch (Exception ignored) {
        }
    }

    public static void drawTitle(DrawContext drawContext, HandledScreen<?> screen, Object containerEntity) {
        int color = 4210752;
        TextRenderer textRenderer = screen.getTextRenderer();
        int rows = screen instanceof GenericContainerScreen screen2 ? screen2.getScreenHandler().getRows() : screen instanceof HopperScreen ? 1 : 3;
        int backgroundHeight = 114 + rows * 18;
        int titleX = (screen.getNavigationFocus().width() - 176) / 2 + 8;
        //物品栏标题
        if (DISPLAY_PLAYER_INVENTORY_TITLE_MODE.getStringValue().equals("all")) {
            int playerInventoryTitleY = (screen.getNavigationFocus().height() - (backgroundHeight)) / 2 + backgroundHeight - 94;
            Text playerInventoryTitle = Text.translatable("key.categories.inventory");
            drawContext.drawText(textRenderer, playerInventoryTitle, titleX, playerInventoryTitleY, color, false);
        }
        //容器
        if (DISPLAY_CONTAINER_TITLE_MODE.getStringValue().equals("all")) {
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
                titleY = titleY - 15;
                color = 0xFF606060;
            } else if (containerEntity instanceof TrappedChestBlockEntity && DISPLAY_TRAPPED_CHEST_TITLE.getBooleanValue()) {
                color = 0xFF9C0000;
            }
            drawContext.drawText(textRenderer, title, titleX, titleY, color, false);
        }
    }

    public static void setTitle(Screen screen, Object containerEntity) {
        ScreenHandlerType<?> type;
        if (!(screen instanceof HandledScreen<?> handledScreen)) return;
        type = ((Accessors.ScreenHandlerAccessor)(handledScreen.getScreenHandler())).inventory_preview_fix_getType();
        //强制显示物品栏标题功能开启后会在drawTitle()方法重新绘制新标题
        if (DISPLAY_PLAYER_INVENTORY_TITLE_MODE.getStringValue().equals("all") || DISPLAY_PLAYER_INVENTORY_TITLE_MODE.getStringValue().equals("no")) {
            ((Accessors.HandledScreenAccessor) handledScreen).inventory_preview_fix_setPlayerInventoryTitle(Text.empty());
        }
        //防止显示所有标题功能让容器有2个标题
        if (DISPLAY_CONTAINER_TITLE_MODE.getStringValue().equals("no") || DISPLAY_CONTAINER_TITLE_MODE.getStringValue().equals("all")) {
            //用 访问加宽器 修改title
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix_setTitle(Text.empty());
        }

        if (containerEntity instanceof TrappedChestBlockEntity && DISPLAY_TRAPPED_CHEST_TITLE.getBooleanValue()) {
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix_setTitle(screen.getTitle().copy().withColor(0xFFFF0000));
            return;
        }

        if (!isLoadedWuTongUI()) return;
        if (screen instanceof BrewingStandScreen && (isChinese() || isEN_US())) {
            //酿造台的小抄之后会将单独渲染（原版有点小bug）
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix_setTitle(Text.empty());
        } else if (containerEntity instanceof BarrelBlockEntity) {
            //让不同语言都能使用(大)木桶和末影箱的GUI
            if (!BARREL_FIXES.getBooleanValue()) return;
            if (type == GENERIC_9X6) {
                ((Accessors.ScreenAccessor) screen).inventory_preview_fix_setTitle(Text.translatable("inventorypreviewpatch.large.barrel.title"));
            } else if (type == GENERIC_9X3) {
                ((Accessors.ScreenAccessor) screen).inventory_preview_fix_setTitle(Text.translatable("inventorypreviewpatch.barrel.title"));
            }
        } else if (containerEntity instanceof EnderChestBlockEntity) {
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix_setTitle(Text.translatable("inventorypreviewpatch.enderchest.title"));
        } else if (containerEntity instanceof AbstractMinecartEntity) {
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix_setTitle(Text.translatable("inventorypreviewpatch.minecart.title"));
        } else if (containerEntity instanceof AbstractBoatEntity) {
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix_setTitle(Text.translatable("inventorypreviewpatch.boat.title"));
        }
    }
}