package inventorypreviewpatch.render;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import inventorypreviewpatch.mixin.Accessors;
import net.minecraft.block.entity.*;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgramKeys;
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

import static inventorypreviewpatch.configs.Configs.Fixes.BARREL_FIXES;
import static inventorypreviewpatch.configs.Configs.Generic.BLANK_TITLE;
import static inventorypreviewpatch.configs.Configs.Generic.DISPLAY_TRAPPED_CHEST_TITLE;
import static inventorypreviewpatch.event.ResourcesLoadedListener.*;
import static inventorypreviewpatch.render.WuTongUIOverlay.PreviewOverlay.*;
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

    public static <T> void setTitle(Screen screen, T containerEntity) {
        if (!(screen instanceof HandledScreen<?> handledScreen) || containerEntity == null) return;

        if (BLANK_TITLE.getBooleanValue()) {
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix$setTitle(Text.empty());
        }

        if (containerEntity instanceof TrappedChestBlockEntity && DISPLAY_TRAPPED_CHEST_TITLE.getBooleanValue()) {
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix$setTitle(screen.getTitle().copy().withColor(0xFFFF0000));
            return;
        }

        if (!isLoadedWuTongUI()) return;
        if (screen instanceof BrewingStandScreen
                && containerEntity instanceof BrewingStandBlockEntity brewing
                && brewing.getCustomName() == null
                && (isChinese() || isEN_US())
        ) {
            //酿造台的小抄之后会将单独渲染（原版有点小bug）
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix$setTitle(Text.empty());
        } else if (containerEntity instanceof BarrelBlockEntity) {  //让不同语言都能使用(大)木桶和末影箱的GUI
            if (!BARREL_FIXES.getBooleanValue()) return;
            ScreenHandlerType<?> type = ((Accessors.ScreenHandlerAccessor)(handledScreen.getScreenHandler())).inventory_preview_fix$getType();
            if (type == GENERIC_9X6) {
                ((Accessors.ScreenAccessor) screen).inventory_preview_fix$setTitle(Text.translatable("inventorypreviewpatch.large.barrel.title"));
            } else if (type == GENERIC_9X3) {
                ((Accessors.ScreenAccessor) screen).inventory_preview_fix$setTitle(Text.translatable("inventorypreviewpatch.barrel.title"));
            }
        } else if (containerEntity instanceof EnderChestBlockEntity) {
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix$setTitle(Text.translatable("inventorypreviewpatch.enderchest.title"));
        } else if (containerEntity instanceof AbstractMinecartEntity) {
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix$setTitle(Text.translatable("inventorypreviewpatch.minecart.title"));
        } else if (containerEntity instanceof AbstractBoatEntity) {
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix$setTitle(Text.translatable("inventorypreviewpatch.boat.title"));
        } else if (!(screen instanceof GenericContainerScreen)) {
            TextRenderer textRenderer = screen.getTextRenderer();
            int backgroundHeight = 114 + 18 * (screen instanceof HopperScreen? 1:3);
            int titleX = (((Accessors.HandledScreenAccessor)screen).inventory_preview_fix$getBackgroundWidth() - textRenderer.getWidth(screen.getTitle())) / 2;
            int titleY = (((Accessors.HandledScreenAccessor)screen).inventory_preview_fix$getBackgroundHeight() - backgroundHeight) / 2 - 9;
            ((Accessors.HandledScreenAccessor)screen).inventory_preview_fix$setTitleX(titleX);
            ((Accessors.HandledScreenAccessor)screen).inventory_preview_fix$setTitleY(titleY);
        }
    }
}