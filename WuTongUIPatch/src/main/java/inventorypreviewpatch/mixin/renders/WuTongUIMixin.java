package inventorypreviewpatch.mixin.renders;

import fi.dy.masa.malilib.render.InventoryOverlay;
import inventorypreviewpatch.render.WuTongUIOverlay;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static inventorypreviewpatch.ModUtils.isResourcePackLoaded;
import static inventorypreviewpatch.configs.Configs.Fixes.Inventory_Preview_Fix_Mode;

@Mixin(value = InventoryOverlay.class, priority = 400)
public class WuTongUIMixin {
    @Inject(method = "renderInventoryBackground", at = @At("HEAD"), cancellable = true)
    private static void renderInventoryBackground(InventoryOverlay.InventoryRenderType type, int x, int y, int slotsPerRow, int totalSlots, MinecraftClient mc, CallbackInfo ci) {
        if (Inventory_Preview_Fix_Mode.getStringValue() .equals("wutong") && isResourcePackLoaded()) {
            //熔炉的GUI不在这渲染
            if (type == InventoryOverlay.InventoryRenderType.FURNACE) return;
            ci.cancel();
            WuTongUIOverlay.renderInventoryBackground(type, x, y, slotsPerRow, totalSlots, mc);
        }
    }

   /* @Inject(method = "renderEquipmentOverlayBackground", at = @At(value = "HEAD"), cancellable = true)
    private static void renderEquipmentOverlayBackground(int x, int y, LivingEntity entity, DrawContext drawContext, CallbackInfo ci) {
        if (!INVENTORY_PREVIEW_FIX.getBooleanValue()) {
            ci.cancel();
            RenderUtils.color(1f, 1f, 1f, 1f);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            BuiltBuffer builtBuffer;

            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
            RenderUtils.bindTexture(Identifier.ofVanilla("textures/gui/container/fixed_dispenser.png"));

            RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 50, 83, buffer); // top-left (main part)
            RenderUtils.drawTexturedRectBatched(x + 50, y, 173, 0, 3, 83, buffer); // right edge top
            RenderUtils.drawTexturedRectBatched(x, y + 83, 0, 163, 50, 3, buffer); // bottom edge left
            RenderUtils.drawTexturedRectBatched(x + 50, y + 83, 173, 163, 3, 3, buffer); // bottom right corner

            for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18) {
                RenderUtils.drawTexturedRectBatched(x + xOff, y + yOff, 61, 16, 18, 18, buffer);
            }

            // Main hand and offhand
            RenderUtils.drawTexturedRectBatched(x + 28, y + 2 * 18 + 7, 61, 16, 18, 18, buffer);
            RenderUtils.drawTexturedRectBatched(x + 28, y + 3 * 18 + 7, 61, 16, 18, 18, buffer);

            try {
                builtBuffer = buffer.end();
                BufferRenderer.drawWithGlobalProgram(builtBuffer);
                builtBuffer.close();
            } catch (Exception ignored) {
            }

            if (entity.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty()) {
                renderBackgroundSlotAt(x + 28 + 1, y + 3 * 18 + 7 + 1, TEXTURE_EMPTY_SHIELD, drawContext);
            }

            for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18) {
                final EquipmentSlot eqSlot = VALID_EQUIPMENT_SLOTS[i];

                if (entity.getEquippedStack(eqSlot).isEmpty()) {
                    Identifier texture = EMPTY_SLOT_TEXTURES[eqSlot.getEntitySlotId()];
                    renderBackgroundSlotAt(x + xOff + 1, y + yOff + 1, texture, drawContext);
                }
            }
        }
    }

    @Unique private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
    @Unique private static final Identifier[] EMPTY_SLOT_TEXTURES = new Identifier[]
            {
                    // 1.21.4+
                    Identifier.ofVanilla("container/slot/boots"),
                    Identifier.ofVanilla("container/slot/leggings"),
                    Identifier.ofVanilla("container/slot/chestplate"),
                    Identifier.ofVanilla("container/slot/helmet")
            };*/
}

