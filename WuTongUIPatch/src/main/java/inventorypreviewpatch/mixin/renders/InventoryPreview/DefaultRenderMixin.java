package inventorypreviewpatch.mixin.renders.InventoryPreview;

import fi.dy.masa.malilib.render.InventoryOverlay;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.function.Function;

import static fi.dy.masa.malilib.render.RenderUtils.bindTexture;
import static inventorypreviewpatch.configs.Configs.Fixes.Inventory_Preview_Fix_Mode;
import static inventorypreviewpatch.event.ResourcesLoadedListener.isLoadedWuTongUI;

@Mixin(value = InventoryOverlay.class, priority = 500)
public class DefaultRenderMixin {

    @Redirect(method = "renderLockedSlotAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIIII)V"))
    private static void drawGuiTexture(DrawContext instance, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height, int color) {
        if (Inventory_Preview_Fix_Mode.getStringValue().equals("vanilla") || (Inventory_Preview_Fix_Mode.getStringValue().equals("wutong") && !isLoadedWuTongUI)) {
            if (Objects.equals(sprite.getPath(), "container/crafter/disabled_slot")) {
                sprite = Identifier.ofVanilla("container/crafter/fixed_disabled_slot");
                instance.drawGuiTexture(RenderLayer::getGuiTextured, sprite, 0, 0, 18, 18, color);
            }
        } else {
            instance.drawGuiTexture(RenderLayer::getGuiTextured, sprite, 0, 0, 18, 18, color);
        }
    }

    @Redirect(method = {"renderInventoryBackground", "renderInventoryBackground27", "renderInventoryBackground54"},
            at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/render/RenderUtils;bindTexture(Lnet/minecraft/util/Identifier;)V"))
    private static void redirectTextureI(Identifier texture) {
        if (Inventory_Preview_Fix_Mode.getStringValue().equals("vanilla") || (Inventory_Preview_Fix_Mode.getStringValue().equals("wutong") && !isLoadedWuTongUI)) {
            rredirectTexture(texture);
        } else {
            bindTexture(texture);
        }
    }

    @Redirect(method = "renderEquipmentOverlayBackground",
            at = @At(value = "INVOKE", target = "Lfi/dy/masa/malilib/render/RenderUtils;bindTexture(Lnet/minecraft/util/Identifier;)V"))
    private static void redirectTextureII(Identifier texture) {
        rredirectTexture(texture);
    }

    @Unique
    private static void rredirectTexture(Identifier texture) {
        if (texture.getPath().startsWith("textures/gui/container/")) {
            int insertPos = texture.getPath().indexOf("container/") + 10;
            StringBuilder sb = new StringBuilder(texture.getPath());
            sb.insert(insertPos, "fixed_");
            Identifier newTexture = Identifier.ofVanilla(sb.toString());
            bindTexture(newTexture);
        }
    }

}

