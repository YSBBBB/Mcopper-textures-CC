package inventorypreviewpatch.mixin.renders.InventoryPreview;

import fi.dy.masa.malilib.render.InventoryOverlay;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static inventorypreviewpatch.configs.Configs.Fixes.INVENTORY_PREVIEW_FIX_MODE;

@Mixin(value = InventoryOverlay.class, priority = 500)
public class DefaultRenderMixin {
    @Unique
    private static final Identifier VANILLA_TEXTURE_DISPENSER = Identifier.ofVanilla("textures/gui/container/fixed_dispenser.png");
    @Unique
    private static final Identifier TEXTURE_LOCKED_SLOT = Identifier.ofVanilla("container/crafter/fixed_disabled_slot");

    @ModifyArgs(method = "renderLockedSlotAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIIII)V"))
    private static void injected(Args args) {
        if (!(INVENTORY_PREVIEW_FIX_MODE.getStringValue().equals("vanilla") || (INVENTORY_PREVIEW_FIX_MODE.getStringValue().equals("wutong")))) return;
        args.set(1, TEXTURE_LOCKED_SLOT);
    }

    @ModifyArgs(method = "renderEquipmentOverlayBackground", at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/render/RenderUtils;bindTexture(Lnet/minecraft/util/Identifier;)V"))
    private static void bindTexture(Args args) {
        args.set(0, VANILLA_TEXTURE_DISPENSER);
    }
}

