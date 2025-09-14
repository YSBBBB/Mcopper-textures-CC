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

@Mixin(value = ShulkerBoxScreen.class, priority = 1)
public class ShulkerBoxScreenMixin {
    @Unique
    private static final Identifier TEXTURE_SHULKER_BOX = Identifier.ofVanilla("textures/gui/container/shulker_box.png");
    @Unique
    private static final Identifier TEXTURE_SHULKER_BOX_GRAY = Identifier.of("inventorypreviewpatch", "textures/gui/container/shulker_box_gray_sprite.png");

    @Inject(method = "drawBackground", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void drawBackgroundWithColor(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci, int i, int j) {
        if (!RENDER_SHULKERBOX_COLOR_MODE.getStringValue().equals("no")) {
            if (HitListener.getInstance().blockEntity instanceof ShulkerBoxBlockEntity sbbe) {
                DyeColor color = sbbe.getColor();
                Identifier sprite = color == null ? TEXTURE_SHULKER_BOX : isLoadedWuTongUI ? TEXTURE_SHULKER_BOX_GRAY : TEXTURE_SHULKER_BOX;
                int colors = switch (color) {
                    case null -> isLoadedWuTongUI ? 0xFFFFFFFF : 0xFFA587BB;
                    //太黑会糊成一坨
                    case BLACK -> 0xFF262626;
                    default -> color.getEntityColor();
                };
                //渲染上半部分
                context.drawTexture(RenderLayer::getGuiTextured, sprite, i, j, 0.0F, 0.0F, 176, 77, 256, 256, colors);
                //渲染下半部分
                if (!RENDER_SHULKERBOX_COLOR_MODE.getStringValue().equals("all")) return;
                colors = color == null && isLoadedWuTongUI ? 0xFFA587BB : colors;
                context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_SHULKER_BOX, i, j + 77, 0.0F, 77.0F, 176, 89, 256, 256, colors);
            }
        }
    }
}
