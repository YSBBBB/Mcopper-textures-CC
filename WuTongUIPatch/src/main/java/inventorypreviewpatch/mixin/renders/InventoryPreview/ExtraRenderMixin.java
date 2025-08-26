package inventorypreviewpatch.mixin.renders.InventoryPreview;

import fi.dy.masa.malilib.render.InventoryOverlayScreen;
import fi.dy.masa.malilib.util.GuiUtils;
import inventorypreviewpatch.event.ResourcesLoadedListener;
import inventorypreviewpatch.helper.MethodExecuteHelper;
import inventorypreviewpatch.render.WuTongUIOverlay;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

import static fi.dy.masa.malilib.render.InventoryOverlay.*;
import static inventorypreviewpatch.configs.Configs.Fixes.Inventory_Preview_Fix_Mode;
import static inventorypreviewpatch.configs.Configs.Fixes.PREVENT_PREVIEWING_OWN_BACKPACK;
import static inventorypreviewpatch.configs.Configs.Generic.DISPLAY_BREWING_STAND_PROGRESS;
import static inventorypreviewpatch.configs.Configs.Generic.DISPLAY_FURNACE_PROGRESS;
import static inventorypreviewpatch.render.WuTongUIOverlayHandler.renderFrame;

@Mixin(InventoryOverlayScreen.class)
public class ExtraRenderMixin {

    @Unique
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    @Unique
    public boolean shulkerBGColors;
    @Unique
    public Context previewData;

    @Unique
    private static boolean BlockEntityTypeFiltering(BlockEntity be) {
        return switch (be) {
            case ChestBlockEntity ignore -> true;
            case BarrelBlockEntity ignore -> true;
            case AbstractFurnaceBlockEntity ignore -> true;
            case ShulkerBoxBlockEntity ignore -> true;
            case BrewingStandBlockEntity ignore -> true;
            case null, default -> false;
        };
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void renderAtHead(DrawContext drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        //防止某些情况下预览到玩家自己的背包(通常是玩家移动速度过快或前后速度差过大时)，该功能不会影响背包预览
        if (PREVENT_PREVIEWING_OWN_BACKPACK.getBooleanValue()) {
            if (previewData != null && mc.player != null && previewData.entity() != null) {
                if (previewData.entity().getUuid().equals(mc.player.getUuid())) {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderAtTail(DrawContext drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MethodExecuteHelper.startExecute("inventory_preview", -1);  //判断该方法是否正在执行
        if (previewData != null) {
            BlockEntity be = previewData.be();
            NbtCompound nbt = previewData.nbt();
            final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
            final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
            int totalSlots = previewData.inv() == null ? 0 : previewData.inv().size();
            //减少一个条件判断，因为后续方法中inv为null并没有什么影响
            if (totalSlots > 0/* && previewData.inv() != null*/) {
                final InventoryRenderType type = getBestInventoryType(previewData.inv(), previewData.nbt() != null ? previewData.nbt() : new NbtCompound(), previewData);
                final InventoryProperties props = getInventoryPropsTemp(type, totalSlots);
                int xInv = xCenter - (props.width / 2);
                int yInv = yCenter - props.height - 6;
                //船和矿车
                if (Inventory_Preview_Fix_Mode.getStringValue().equals("wutong") && ResourcesLoadedListener.isLoadedWuTongUI) {
                    if (be == null && nbt != null && nbt.contains("id")) {
                        Identifier MINECART = Identifier.ofVanilla("textures/font/b118.png");
                        Identifier BOAT = Identifier.ofVanilla("textures/font/b117.png");
                        if (nbt.getString("id").equals("minecraft:chest_minecart")) {
                            renderFrame(null, null, xInv, yInv, 0, 27, 2);
                            drawContext.drawTexture(RenderLayer::getGuiTextured, MINECART, xCenter - 8, yCenter - 83, 0.0F, 0.0F, 16, 16, 16, 16);
                        } else if (nbt.getString("id").equals("minecraft:hopper_minecart")) {
                            drawContext.drawTexture(RenderLayer::getGuiTextured, MINECART, xCenter - 8, yCenter - 49, 0.0F, 0.0F, 16, 16, 16, 16);
                        } else if (nbt.getString("id").equals("minecraft:bamboo_chest_raft") || nbt.getString("id").equals("minecraft:oak_chest_boat")) {
                            renderFrame(null, null, xInv, yInv, 0, 27, 2);
                            drawContext.drawTexture(RenderLayer::getGuiTextured, BOAT, xCenter - 8, yCenter - 83, 0.0F, 0.0F, 16, 16, 16, 16);
                        }
                        renderInventoryStacks(type, previewData.inv(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, 0, totalSlots, new HashSet<>(), mc, drawContext, mouseX, mouseY);
                        return;
                    }
                    //方块实体
                    if (BlockEntityTypeFiltering(be)) {
                        //潜影盒预览添加底色
                        assert be != null;
                        if (be.getCachedState().getBlock() instanceof ShulkerBoxBlock sbb) {
                            WuTongUIOverlay.setShulkerboxBackgroundTintColor(sbb, shulkerBGColors);
                        }
                        renderFrame(type, be, xInv, yInv, 0, totalSlots, 2);
                        switch (be) {
                            case BarrelBlockEntity bbe -> renderFrame(type, bbe, xInv, yInv, 0, totalSlots, 3);
                            case AbstractFurnaceBlockEntity abe when DISPLAY_FURNACE_PROGRESS.getBooleanValue() ->
                                    renderFrame(null, abe, xInv, yInv, 0, 0, 4);
                            case BrewingStandBlockEntity bsbe when DISPLAY_BREWING_STAND_PROGRESS.getBooleanValue() ->
                                    renderFrame(null, bsbe, xInv, yInv, 0, 0, 5);
                            default -> {
                            }
                        }
                        renderInventoryStacks(type, previewData.inv(), xInv + props.slotOffsetX, yInv + props.slotOffsetY, props.slotsPerRow, 0, totalSlots, new HashSet<>(), mc, drawContext, mouseX, mouseY);
                    }
                } else {
                    if (be != null) {
                        if (be instanceof AbstractFurnaceBlockEntity abe && DISPLAY_FURNACE_PROGRESS.getBooleanValue()) {
                            renderFrame(null, abe, xInv, yInv, 0, 0, 4);
                        } else if (be instanceof BrewingStandBlockEntity bsbe && DISPLAY_BREWING_STAND_PROGRESS.getBooleanValue()) {
                            renderFrame(null, bsbe, xInv, yInv, 0, 0, 5);
                        }
                    }
                }
            }
        }
    }
}
