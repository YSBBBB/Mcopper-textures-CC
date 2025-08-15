package inventorypreviewpatch.mixin.renders;

import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.InventoryOverlayScreen;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.WorldUtils;
import inventorypreviewpatch.ModUtils;
import inventorypreviewpatch.render.WuTongUIOverlay;
import inventorypreviewpatch.render.WuTongUIOverlayHandler;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

import static fi.dy.masa.malilib.render.InventoryOverlay.*;
import static inventorypreviewpatch.configs.Configs.Fixes.Inventory_Preview_Fix_Mode;
import static inventorypreviewpatch.configs.Configs.Generic.DISPLAY_BREWING_STAND_PROGRESS;
import static inventorypreviewpatch.configs.Configs.Generic.DISPLAY_FURNACE_PROGRESS;

@Mixin(InventoryOverlayScreen.class)
public class ExtraRenderMixin {
    @Unique
    public boolean shulkerBGColors;

    @Unique
    public InventoryOverlay.Context previewData;

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!Inventory_Preview_Fix_Mode.getStringValue() .equals("wutong") || !ModUtils.isResourcePackLoaded()) return;
        BlockEntity be = previewData.be();
        if (
                be instanceof ChestBlockEntity
                || be instanceof BarrelBlockEntity
                || be instanceof AbstractFurnaceBlockEntity
                || be instanceof ShulkerBoxBlockEntity
                || be instanceof BrewingStandBlockEntity
        ) {
        MinecraftClient mc = MinecraftClient.getInstance();
        World world = WorldUtils.getBestWorld(mc);
        if (previewData != null && world != null) {
            int totalSlots = previewData.inv() == null ? 0 : previewData.inv().size();
            if (totalSlots > 0 && previewData.inv() != null) {
                final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
                final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
                final InventoryRenderType type = getBestInventoryType(previewData.inv(), previewData.nbt() != null ? previewData.nbt() : new NbtCompound(), previewData);
                final InventoryProperties props = getInventoryPropsTemp(type, totalSlots);
                int xInv = xCenter - (props.width / 2);
                int yInv = yCenter - props.height - 6;

                if (previewData.be().getCachedState().getBlock() instanceof ShulkerBoxBlock sbb) {
                    WuTongUIOverlay.setShulkerboxBackgroundTintColor(sbb, shulkerBGColors);
                }

                WuTongUIOverlay.renderSpecialInventoryBackground(be, xInv, yInv, totalSlots);
                if (be instanceof AbstractFurnaceBlockEntity abe && DISPLAY_FURNACE_PROGRESS.getBooleanValue()) {
                    WuTongUIOverlayHandler.RenderFurnaceProcessingProgress(abe,xInv,yInv);
                } else if (be instanceof BrewingStandBlockEntity bsbe && DISPLAY_BREWING_STAND_PROGRESS.getBooleanValue()) {
                    WuTongUIOverlayHandler.RenderBrewingStandProcessingProgress(bsbe,xInv,yInv);
                }

                renderInventoryStacks(type, previewData.inv(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, 0, totalSlots, new HashSet<>(), mc, drawContext, mouseX, mouseY);
            }
            }
        }
    }
}