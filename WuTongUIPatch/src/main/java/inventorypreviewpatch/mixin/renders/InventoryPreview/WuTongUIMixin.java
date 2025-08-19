package inventorypreviewpatch.mixin.renders.InventoryPreview;

import fi.dy.masa.malilib.render.InventoryOverlay;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static inventorypreviewpatch.ModUtils.isLoadedWuTongUI;
import static inventorypreviewpatch.configs.Configs.Fixes.Inventory_Preview_Fix_Mode;
import static inventorypreviewpatch.render.WuTongUIOverlayHandler.renderFrame;

@Mixin(value = InventoryOverlay.class)
public class WuTongUIMixin {
    @Inject(method = "renderInventoryBackground", at = @At("HEAD"), cancellable = true)
    private static void renderInventoryBackground(InventoryOverlay.InventoryRenderType type, int x, int y, int slotsPerRow, int totalSlots, MinecraftClient mc, CallbackInfo ci) {
        if (Inventory_Preview_Fix_Mode.getStringValue() .equals("wutong") && isLoadedWuTongUI()) {
            //熔炉的GUI不在这渲染
            if (type == InventoryOverlay.InventoryRenderType.FURNACE) return;
            ci.cancel();
            renderFrame(type, null, x, y, slotsPerRow, totalSlots, 1);
        }
    }
}

