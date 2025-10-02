package inventorypreviewpatch.mixin.renders;

import inventorypreviewpatch.event.HitListener;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static inventorypreviewpatch.configs.Configs.Generic.RENDER_SHULKERBOX_COLOR_MODE;
import static inventorypreviewpatch.event.ResourcesLoadedListener.isLoadedWuTongUI;
import static inventorypreviewpatch.render.WuTongUIOverlay.getRender_correction;

@Mixin(value = ShulkerBoxScreen.class, priority = 1)
public class ShulkerBoxScreenMixin {
    @Unique
    private static final Identifier TEXTURE_SHULKER_BOX = Identifier.ofVanilla("textures/gui/container/shulker_box.png");
    @Unique
    private static final Identifier TEXTURE_SHULKER_BOX_GRAY = Identifier.of("inventorypreviewpatch", "textures/gui/container/shulker_box_gray_sprite.png");
    @Unique
    private static DyeColor CACHE_BOX_COLOR;
    @Unique
    private static int CACHE_COLOR;

    @Inject(method = "drawBackground", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void drawBackgroundWithColor(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci, int i, int j) {
        if (RENDER_SHULKERBOX_COLOR_MODE.getStringValue().equals("no")) return;
        if (!(HitListener.getInstance().getHitResult().be() instanceof ShulkerBoxBlockEntity sbbe)) return;
        if (CACHE_BOX_COLOR != sbbe.getColor()) {
            CACHE_BOX_COLOR = sbbe.getColor();
            calculateColor(CACHE_BOX_COLOR);
        }
        Identifier sprite = CACHE_BOX_COLOR == null ? TEXTURE_SHULKER_BOX : isLoadedWuTongUI() ? TEXTURE_SHULKER_BOX_GRAY : TEXTURE_SHULKER_BOX;
        //渲染上半部分
        context.drawTexture(RenderLayer::getGuiTextured, sprite, i, j, 0.0F, 0.0F, 176, 77, 256, 256, CACHE_COLOR);
        if (!RENDER_SHULKERBOX_COLOR_MODE.getStringValue().equals("all")) return;
        //渲染下半部分
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SHULKER_BOX, i, j + 77, 0.0F, 77.0F, 176, 89, 256, 256, CACHE_COLOR);
    }

    @Unique
    private static void calculateColor(DyeColor CACHE_BOX_COLOR) {
        //太黑会糊成一坨
        if (isLoadedWuTongUI()) {
            CACHE_COLOR = switch (CACHE_BOX_COLOR) {
                case null -> 0xFFFFFFFF;
                case BLACK -> 0xFF262626;
                default -> CACHE_BOX_COLOR.getEntityColor();
            };
        } else {
            String colorId = "color_" + (CACHE_BOX_COLOR != null ? CACHE_BOX_COLOR.toString() : "primeval");
            CACHE_COLOR = getRender_correction().containsKey(colorId)?
                    getRender_correction().get(colorId)[0] :
                    CACHE_BOX_COLOR == null? 0xFFA587BB : CACHE_BOX_COLOR == DyeColor.BLACK? 0xFF262626 : CACHE_BOX_COLOR.getEntityColor();
        }
    }
}
