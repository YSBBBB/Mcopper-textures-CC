package inventorypreviewpatch.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import inventorypreviewpatch.ModUtils;
import inventorypreviewpatch.mixin.Accessors;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SmithingTableBlock;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.*;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static fi.dy.masa.malilib.render.InventoryOverlay.*;
import static fi.dy.masa.malilib.render.RenderUtils.color;
import static fi.dy.masa.malilib.render.RenderUtils.getColorComponents;
import static inventorypreviewpatch.ModUtils.isContainerScreen;
import static inventorypreviewpatch.configs.Configs.Fixes.INVENTORY_PREVIEW_FIX_MODE;
import static inventorypreviewpatch.event.ResourcesLoadedListener.isChinese;
import static inventorypreviewpatch.event.ResourcesLoadedListener.isEN_US;
import static inventorypreviewpatch.render.WuTongUIOverlayHandler.renderFrame;

public class WuTongUIOverlay {

    private static final Identifier TEXTURE_BREWING_STAND = Identifier.ofVanilla("textures/gui/container/brewing_stand.png");
    private static final Identifier TEXTURE_FIXED_GENERIC_54 = Identifier.ofVanilla("textures/gui/container/fixed_generic_54.png");
    private static final Identifier TEXTURE_ENDER_CHEST = Identifier.ofVanilla("textures/font/b111.png");
    private static final Identifier TEXTURE_GENERIC_54 = Identifier.ofVanilla("textures/gui/container/generic_54.png");
    private static final Identifier TEXTURE_BARREL = Identifier.ofVanilla("textures/font/b116.png");
    private static final Identifier TEXTURE_SHULKER_BOX_GRAY = Identifier.of("inventorypreviewpatch", "textures/gui/container/shulker_box_gray_sprite.png");
    private static final Identifier TEXTURE_SHULKER_BOX = Identifier.ofVanilla("textures/gui/container/shulker_box.png");
    private static final Identifier TEXTURE_FURNACE = Identifier.ofVanilla("textures/gui/container/furnace.png");
    private static final Identifier TEXTURE_SMOKER = Identifier.ofVanilla("textures/gui/container/smoker.png");
    private static final Identifier TEXTURE_BLAST_FURNACE = Identifier.ofVanilla("textures/gui/container/blast_furnace.png");

    private static final Identifier VANILLA_TEXTURE_DISPENSER = Identifier.ofVanilla("textures/gui/container/fixed_dispenser.png");
    private static final Identifier VANILLA_TEXTURE_BREWING_STAND = Identifier.ofVanilla("textures/gui/container/fixed_brewing_stand.png");
    private static final Identifier VANILLA_TEXTURE_FURNACE = Identifier.ofVanilla("textures/gui/container/fixed_furnace.png");
    private static final Identifier VANILLA_TEXTURE_HOPPER = Identifier.ofVanilla("textures/gui/container/fixed_hopper.png");
    private static final Identifier VANILLA_TEXTURE_SINGLE_CHEST = Identifier.ofVanilla("textures/gui/container/fixed_shulker_box.png");

    public static <T> Text amendTitle(Screen screen, T containerEntity) {
        Text title = Text.empty();
        if (!isContainerScreen(screen)) return title;
        boolean isLargeInventory = ((HandledScreen<?>) screen).getScreenHandler().slots.size() >= 54;
        boolean useSpecificLanguage = isChinese() || isEN_US();
        title = switch (containerEntity) {
            case AnvilBlock ignore -> Text.translatable("block.minecraft.anvil");
            case SmithingTableBlock ignore -> Text.translatable("block.minecraft.smithing_table");
            case ChestBlockEntity ignore -> useSpecificLanguage ? Text.of((isLargeInventory ?
                    (isChinese() ? "大型箱子" : "Large Chest")
                    : (isChinese() ? "箱子" : "Chest")))
                    : Text.translatable(isLargeInventory ? "container.chestDouble" : "container.chest");
            case BarrelBlockEntity ignore -> Text.translatable(useSpecificLanguage
                    ? (isLargeInventory
                    ? "container.large.barrel"
                    : "container.small.barrel")
                    : "container.barrel");
            case ChestMinecartEntity ignore -> Text.translatable(useSpecificLanguage ?
                    isChinese() ? "运输矿车" : "Minecart with Chest"
                    : "entity.minecraft.chest_minecart");
            case HopperMinecartEntity ignore -> useSpecificLanguage ? Text.of(isChinese()
                    ? "漏斗矿车" : "Minecart with Hopper")
                    : Text.translatable("entity.minecraft.hopper_minecart");
            case AbstractChestBoatEntity boatEntity -> switch (boatEntity) {
                case ChestRaftEntity ignore -> useSpecificLanguage ? Text.of(isChinese()
                        ? "筏载箱子"
                        : "Chest on Bamboo Raft")
                        : Text.translatable("entity.minecraft.bamboo_chest_raft");
                case ChestBoatEntity ignore -> useSpecificLanguage ? Text.of(isChinese()
                        ? "船载箱子"
                        : "Chest on Boat")
                        : Text.translatable("entity.minecraft.chest_boat");
                default -> Text.empty();
            };
            case EnderChestBlockEntity ignore -> useSpecificLanguage ? Text.of(isChinese()
                    ? "末影箱"
                    : "Ender Chest")
                    : Text.translatable("container.enderchest");
            default -> {
                if (containerEntity instanceof BlockEntity be) {
                    Block block = be.getCachedState().getBlock();
                    yield block.getName() != null ? block.getName() : block.asItem().getName();
                } else if (containerEntity instanceof Entity entity) {
                    yield entity.getName();
                } else if (containerEntity instanceof Block block) {
                    yield block.getName() != null ? block.getName() : block.asItem().getName();
                } else {
                    yield Text.empty();
                }
            }
        };
        return title;
    }

    public static Map<String, int[]> getRender_correction() {
        return PreviewOverlay.render_correction;
    }

    public static class PreviewOverlay {
        private static final Map<String, int[]> render_correction = new HashMap<>();

        public static void setShulkerboxBackgroundTintColor(@Nullable ShulkerBoxBlock block, boolean useBgColors) {
            if (block != null && useBgColors) {
                float[] colors = switch (INVENTORY_PREVIEW_FIX_MODE.getStringValue()) {
                    case "wutong" -> {
                        //原图用材质包里的
                        final DyeColor dye = block.getColor();
                        yield dye == null?
                                new float[] {1f, 1f, 1f, 1f} :
                                dye == DyeColor.BLACK ? new float[]{0.15f, 0.15f, 0.15f} : getColorComponents(dye.getEntityColor());
                        //太黑会糊成一坨,单独给它调一个
                    }
                    case "customization" -> {
                        final DyeColor dye = block.getColor();
                        String colorId = "color_" + (dye != null ? dye.toString() : "primeval");
                        if (render_correction.containsKey(colorId)) {
                            int color = render_correction.get(colorId)[0];
                            yield getColorComponents(color);
                        } else {
                            yield getColorComponents(dye != null ? dye.getEntityColor() : DyeColor.PURPLE.getEntityColor());
                        }
                    }
                    default -> {
                        final DyeColor dye = block.getColor() != null ? block.getColor() : DyeColor.PURPLE;
                        yield getColorComponents(dye.getEntityColor());
                    }
                };
                color(colors[0], colors[1], colors[2], 1f);
            } else {
                color(1f, 1f, 1f, 1f);
            }
        }

        public static void loadCustomMaterialParameters(JsonObject object) {
            render_correction.clear();
            if (object == null) return;
            if (object.has("gui_correction")) {
                JsonArray gui_correction = object.get("gui_correction").getAsJsonArray();
                for (int i = 0; i < gui_correction.size(); i++) {
                    JsonObject subObject = gui_correction.get(i).getAsJsonObject();
                    if (!subObject.has("id")) continue;
                    int width = subObject.has("width") ? subObject.get("width").getAsInt() : 176;
                    int height = subObject.has("height") ? subObject.get("height").getAsInt() : 88;
                    int textureWidth = subObject.has("textureWidth") ? subObject.get("textureWidth").getAsInt() : 176;
                    int textureHeight = subObject.has("textureHeight") ? subObject.get("textureHeight").getAsInt() : 88;
                    int x_offset = subObject.has("x_offset") ? subObject.get("x_offset").getAsInt() : 0;
                    int y_offset = subObject.has("y_offset") ? subObject.get("y_offset").getAsInt() : 0;
                    render_correction.put(subObject.get("id").getAsString(), new int[]{x_offset, y_offset, width, height, textureWidth, textureHeight});
                }
            }
            if (object.has("bg_color")) {
                JsonArray shulkerboxColorCorrection = object.get("bg_color").getAsJsonArray();
                for (int i = 0; i < shulkerboxColorCorrection.size(); i++) {
                    JsonObject subObject = shulkerboxColorCorrection.get(i).getAsJsonObject();
                    if (!subObject.has("id")) continue;
                    int color = subObject.has("color") ? subObject.get("color").getAsInt() : 0xFFFFFFFF;
                    render_correction.put(subObject.get("id").getAsString(), new int[]{color});
                }
            }
        }

        public static void renderInventoryBackground(InventoryRenderType type, int x, int y, int slotsPerRow, int totalSlots, Context previewData, MinecraftClient mc, DrawContext drawContext) {
            if (INVENTORY_PREVIEW_FIX_MODE.getStringValue().equals("wutong")) {
                renderFrame(type, previewData, x, y, slotsPerRow, totalSlots, 1);
                if (previewData.be() instanceof BarrelBlockEntity) {
                    renderFrame(type, previewData, x, y, slotsPerRow, totalSlots, 2);
                }
            } else if (INVENTORY_PREVIEW_FIX_MODE.getStringValue().equals("customization")) {
                if (previewData.nbt() == null) return;
                String id = previewData.nbt().getString("id").replace(':', '-');
                String id_slots = id + totalSlots;
                final Identifier texture_slots = Identifier.of("inventorypreviewpatch", "textures/preview/overlay/" + id_slots + ".png");
                if (mc.getResourceManager().getResource(texture_slots).isPresent()) {
                    int[] correction = {0, 0, 176, 88, 176, 88};
                    if (render_correction.containsKey(id_slots)) {
                        correction = render_correction.get(id_slots);
                    }
                    x = x + correction[0];
                    y = y - correction[1];
                    drawContext.drawTexture(RenderLayer::getGuiTextured, texture_slots, x, y, 0.0F, 0.0F, correction[2], correction[3], correction[4], correction[5]);
                    return;
                }
                final Identifier texture = Identifier.of("inventorypreviewpatch", "textures/preview/overlay/" + id + ".png");
                if (mc.getResourceManager().getResource(texture).isPresent()) {
                    int[] correction = {0, 0, 176, 88, 176, 88};
                    if (render_correction.containsKey(id)) {
                        correction = render_correction.get(id);
                    }
                    x = x + correction[0];
                    y = y - correction[1];
                    drawContext.drawTexture(RenderLayer::getGuiTextured, texture_slots, x, y, 0.0F, 0.0F, correction[2], correction[3], correction[4], correction[5]);
                } else {
                    renderFrame(type, previewData, x, y, slotsPerRow, totalSlots, 5);
                }
            } else if (INVENTORY_PREVIEW_FIX_MODE.getStringValue().equals("vanilla")) {
                renderFrame(type, previewData, x, y, slotsPerRow, totalSlots, 5);
            } else {
                InventoryOverlay.renderInventoryBackground(type, x, y, slotsPerRow, totalSlots, mc);
            }
        }

        public static void renderWuTongInventoryBackground(InventoryRenderType type, Context previewData, int x, int y, int slotsPerRow, int totalSlots, BufferBuilder buffer) {
            BlockEntity be = previewData.be();
            if (be instanceof ShulkerBoxBlockEntity sbbe) {
                //根据颜色选择材质
                RenderUtils.bindTexture(sbbe.getColor() == null ? TEXTURE_SHULKER_BOX : TEXTURE_SHULKER_BOX_GRAY);
                ModUtils.drawTexturedRectBatched(x, y - 9, 0, 0, 176, 16, 1.0f / 256, buffer); // head
                ModUtils.drawTexturedRectBatched(x, y + 7, 0, 17, 176, 54, 1.0f / 256, buffer); // middle
                ModUtils.drawTexturedRectBatched(x, y + 61, 0, 71, 176, 5, 1.0f / 256, buffer); // bottom
                ModUtils.drawTexturedRectBatched(x, y + 66, 0, 75, 176, 2, 1.0f / 256, buffer); // bottom2
            } else if (be instanceof BarrelBlockEntity) {
                //绘制（大）木桶的材质
                RenderUtils.bindTexture(TEXTURE_BARREL);
                if (totalSlots <= 27) {
                    ModUtils.drawTexturedRectBatched(x, y - 10, 0, 0, 176, 81, 1.0f / 176, buffer);
                } else {
                    ModUtils.drawTexturedRectBatched(x, y, 0, 0, 176, 7, 1.0f / 176, buffer); // head
                    ModUtils.drawTexturedRectBatched(x, y + 7, 0, 18, 176, 53, 1.0f / 176, buffer); // middle
                    ModUtils.drawTexturedRectBatched(x, y + 60, 0, 18, 176, 2, 1.0f / 176, buffer); // middle
                    ModUtils.drawTexturedRectBatched(x, y + 62, 0, 18, 176, 61, 1.0f / 176, buffer); // bottom
                }
            } else if (be instanceof AbstractFurnaceBlockEntity) {
                boolean isBlastFurnace = be instanceof BlastFurnaceBlockEntity;
                //根据熔炉类型设定材质
                if (be instanceof BlastFurnaceBlockEntity) {
                    RenderUtils.bindTexture(TEXTURE_BLAST_FURNACE);
                } else if (be instanceof SmokerBlockEntity) {
                    RenderUtils.bindTexture(TEXTURE_SMOKER);
                } else {
                    RenderUtils.bindTexture(TEXTURE_FURNACE);
                }
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, isBlastFurnace ? 56 : 47, 4, buffer); // top (left)
                RenderUtils.drawTexturedRectBatched(x + (isBlastFurnace ? 56 : 47), y, 126, 0, 50, 4, buffer); // top (right）
                RenderUtils.drawTexturedRectBatched(x, y + 4, 0, 13, 4, 64, buffer); // left
                RenderUtils.drawTexturedRectBatched(x + (isBlastFurnace ? 103 : 94), y + 4, 173, 13, 4, 64, buffer); // right
                RenderUtils.drawTexturedRectBatched(x + 4, y + 4, 52, 13, isBlastFurnace ? 99 : 90, 64, buffer); // middle
            } else if (type == InventoryRenderType.BREWING_STAND) {
                RenderUtils.bindTexture(TEXTURE_BREWING_STAND);
                RenderUtils.drawTexturedRectBatched(x + 4, y - 9, 13, 0, 115, 74, buffer); // main
                RenderUtils.drawTexturedRectBatched(x + 63, y + 65, 72, 74, 30, 7, buffer); // bottom
            } else if (type == InventoryRenderType.CRAFTER) {
                RenderUtils.bindTexture(TEXTURE_CRAFTER);
                RenderUtils.drawTexturedRectBatched(x - 5, y - 5, 13, 4, 78, 71, buffer); // middle
                RenderUtils.drawTexturedRectBatched(x - 5, y + 66, 13, 10, 78, 1, buffer); // frame1
                RenderUtils.drawTexturedRectBatched(x - 5, y + 67, 13, 7, 78, 3, buffer); // frame2
                RenderUtils.drawTexturedRectBatched(x - 5, y + 70, 13, 3, 78, 4, buffer); // frame3
            } else if (type == InventoryRenderType.DISPENSER) {
                RenderUtils.bindTexture(TEXTURE_DISPENSER);
                RenderUtils.drawTexturedRectBatched(x, y + 7, 17, 15, 7, 54, buffer); // left
                RenderUtils.drawTexturedRectBatched(x + 61, y + 7, 152, 15, 7, 54, buffer); // right
                RenderUtils.drawTexturedRectBatched(x, y, 17, 0, 34, 7, buffer); // top (left)
                RenderUtils.drawTexturedRectBatched(x + 34, y, 125, 0, 34, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 61, 17, 70, 34, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 34, y + 61, 125, 70, 34, 7, buffer); // bottom (right)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 61, 16, 54, 54, buffer); // middle
            } else if (type == InventoryRenderType.HOPPER) {
                RenderUtils.bindTexture(TEXTURE_HOPPER);
                RenderUtils.drawTexturedRectBatched(x - 36, y - 12, 0, 0, 167, 43, buffer); // main
                RenderUtils.drawTexturedRectBatched(x - 16, y + 31, 0, 43, 140, 1, buffer); // bottom_line
            } else if (type == InventoryRenderType.VILLAGER) {
                RenderUtils.bindTexture(TEXTURE_FIXED_GENERIC_54);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, 79, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 133, 0, 43, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 79, 0, 215, 43, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 43, y + 7, 169, 143, 7, 79, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 7, 17, 36, 72, buffer); // 2x4 slots*/
            } else if (type == InventoryRenderType.ENDER_CHEST) {
                RenderUtils.bindTexture(TEXTURE_ENDER_CHEST);
                ModUtils.drawTexturedRectBatched(x, y - 10, 0, 0, 176, 80, 1.0f / 176, buffer); // main
                ModUtils.drawTexturedRectBatched(x + 85, y + 70, 85, 80, 6, 2, 1.0f / 176, buffer); // latch
            } else if (be instanceof ChestBlockEntity || (previewData.nbt() != null && previewData.entity() == null && previewData.nbt().getString("id").contains("chest"))) {
                //绘制（大）箱子的材质
                boolean isSmallInventory = totalSlots <= 27;
                RenderUtils.bindTexture(TEXTURE_GENERIC_54);
                ModUtils.drawTexturedRectBatched(x, y - (isSmallInventory ? 9 : 0), 0, 0, 176, isSmallInventory ? 16 : 7, 1.0f / 256, buffer); // head
                ModUtils.drawTexturedRectBatched(x, y + 7, 0, 17, 176, isSmallInventory ? 54 : 108, 1.0f / 256, buffer); // middle
                ModUtils.drawTexturedRectBatched(x, y + (isSmallInventory ? 61 : 115), 0, 126, 176, 8, 1.0f / 256, buffer); // bottom
                ModUtils.drawTexturedRectBatched(x + 85, y + (isSmallInventory ? 69 : 123), 85, 134, 6, 2, 1.0f / 256, buffer); // latch
            } else if (type == InventoryRenderType.FIXED_27) {
                RenderUtils.bindTexture(TEXTURE_FIXED_GENERIC_54);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, 61, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 7, 0, 169, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 61, 0, 159, 169, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 169, y + 7, 169, 105, 7, 61, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 7, 17, 162, 54, buffer); // middle
            } else if (type == InventoryRenderType.FIXED_54) {
                RenderUtils.bindTexture(TEXTURE_FIXED_GENERIC_54);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, 115, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 7, 0, 169, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 115, 0, 215, 169, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 169, y + 7, 169, 107, 7, 115, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 7, 17, 162, 108, buffer); // middle
            } else {
                int rows = (int) (Math.ceil((double) totalSlots / (double) slotsPerRow));
                int bgw = Math.min(totalSlots, slotsPerRow) * 18 + 7;
                int bgh = rows * 18 + 7;

                RenderUtils.bindTexture(TEXTURE_FIXED_GENERIC_54);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, bgh, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 176 - bgw, 0, bgw, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + bgh, 0, 215, bgw, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + bgw, y + 7, 169, 222 - bgh, 7, bgh, buffer); // right (bottom)

                for (int row = 0; row < rows; row++) {
                    int rowLen = MathHelper.clamp(totalSlots - (row * slotsPerRow), 1, slotsPerRow);
                    RenderUtils.drawTexturedRectBatched(x + 7, y + row * 18 + 7, 7, 17, rowLen * 18, 18, buffer);

                    if (rows > 1 && rowLen < slotsPerRow) {
                        RenderUtils.drawTexturedRectBatched(x + rowLen * 18 + 7, y + row * 18 + 7, 7, 3, (slotsPerRow - rowLen) * 18, 9, buffer);
                        RenderUtils.drawTexturedRectBatched(x + rowLen * 18 + 7, y + row * 18 + 16, 7, 3, (slotsPerRow - rowLen) * 18, 9, buffer);
                    }
                }
            }
        }

        public static void renderVanillaInventoryBackground(InventoryRenderType type, int x, int y, int slotsPerRow, int totalSlots, BufferBuilder buffer) {
            if (type == InventoryRenderType.FURNACE) {
                RenderUtils.bindTexture(VANILLA_TEXTURE_FURNACE);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 4, 64, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 4, y, 84, 0, 92, 4, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 64, 0, 162, 92, 4, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 92, y + 4, 172, 102, 4, 64, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 4, y + 4, 52, 13, 88, 60, buffer); // middle
            } else if (type == InventoryRenderType.BREWING_STAND) {
                RenderUtils.bindTexture(VANILLA_TEXTURE_BREWING_STAND);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 4, 68, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 4, y, 63, 0, 113, 4, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 68, 0, 162, 113, 4, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 113, y + 4, 172, 98, 4, 68, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 4, y + 4, 13, 13, 109, 64, buffer); // middle
            } else if (type == InventoryRenderType.CRAFTER) {
                // We just hack in the Dispenser Texture, so it displays right.  Easy.
                RenderUtils.bindTexture(VANILLA_TEXTURE_DISPENSER);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, 61, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 115, 0, 61, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 61, 0, 159, 61, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 61, y + 7, 169, 105, 7, 61, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 61, 16, 54, 54, buffer); // middle
            } else if (type == InventoryRenderType.DISPENSER) {
                RenderUtils.bindTexture(VANILLA_TEXTURE_DISPENSER);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, 61, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 115, 0, 61, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 61, 0, 159, 61, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 61, y + 7, 169, 105, 7, 61, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 61, 16, 54, 54, buffer); // middle
            } else if (type == InventoryRenderType.HOPPER) {
                RenderUtils.bindTexture(VANILLA_TEXTURE_HOPPER);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, 25, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 79, 0, 97, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 25, 0, 126, 97, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 97, y + 7, 169, 108, 7, 25, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 43, 19, 90, 18, buffer); // middle
            } else if (type == InventoryRenderType.VILLAGER) {
                RenderUtils.bindTexture(TEXTURE_FIXED_GENERIC_54);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, 79, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 133, 0, 43, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 79, 0, 215, 43, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 43, y + 7, 169, 143, 7, 79, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 7, 17, 36, 72, buffer); // 2x4 slots
            } else if (type == InventoryRenderType.FIXED_27) {
                RenderUtils.bindTexture(VANILLA_TEXTURE_SINGLE_CHEST);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, 61, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 7, 0, 169, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 61, 0, 159, 169, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 169, y + 7, 169, 105, 7, 61, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 7, 17, 162, 54, buffer); // middle
            } else if (type == InventoryRenderType.FIXED_54) {
                RenderUtils.bindTexture(TEXTURE_FIXED_GENERIC_54);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, 115, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 7, 0, 169, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 115, 0, 215, 169, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 169, y + 7, 169, 107, 7, 115, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 7, 17, 162, 108, buffer); // middle
            } else {
                RenderUtils.bindTexture(TEXTURE_FIXED_GENERIC_54);

                int rows = (int) (Math.ceil((double) totalSlots / (double) slotsPerRow));
                int bgw = Math.min(totalSlots, slotsPerRow) * 18 + 7;
                int bgh = rows * 18 + 7;

                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, bgh, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 176 - bgw, 0, bgw, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + bgh, 0, 215, bgw, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + bgw, y + 7, 169, 222 - bgh, 7, bgh, buffer); // right (bottom)

                for (int row = 0; row < rows; row++) {
                    int rowLen = MathHelper.clamp(totalSlots - (row * slotsPerRow), 1, slotsPerRow);
                    RenderUtils.drawTexturedRectBatched(x + 7, y + row * 18 + 7, 7, 17, rowLen * 18, 18, buffer);

                    if (rows > 1 && rowLen < slotsPerRow) {
                        RenderUtils.drawTexturedRectBatched(x + rowLen * 18 + 7, y + row * 18 + 7, 7, 3, (slotsPerRow - rowLen) * 18, 9, buffer);
                        RenderUtils.drawTexturedRectBatched(x + rowLen * 18 + 7, y + row * 18 + 16, 7, 3, (slotsPerRow - rowLen) * 18, 9, buffer);
                    }
                }
            }
        }

        public static void renderWuTongBarrelBackground(int x, int y, int totalSlots, BufferBuilder buffer) {
            boolean isSmallInventory = totalSlots == 27;
            RenderUtils.bindTexture(TEXTURE_GENERIC_54);
            ModUtils.drawTexturedRectBatched(x + 7, y + 7, 7, 17, 162, isSmallInventory ? 54 : 108, 1.0f / 256, buffer);
        }

        public static void renderFurnaceProgress(AbstractFurnaceBlockEntity furnace, int x, int y, BufferBuilder buffer) {
            PropertyDelegate propertyDelegate = ((Accessors.AbstractFurnaceBlockEntityAccessor) furnace).inventory_preview_fix_getPropertyDelegate();
            boolean isBurning = propertyDelegate.get(0) > 0;
            int i = propertyDelegate.get(2);
            int j = propertyDelegate.get(3);
            int k = propertyDelegate.get(1);
            if (k == 0) {
                k = 200;
            }
            float cookProgress = j != 0 && i != 0 ? MathHelper.clamp((float) i / j, 0.0F, 1.0F) : 0.0f;
            float fuelProgress = MathHelper.clamp((float) propertyDelegate.get(0) / k, 0.0F, 1.0F);

            RenderUtils.bindTexture(TEXTURE_FURNACE);
            if (isBurning) {
                int l = MathHelper.ceil(fuelProgress * 13.0F) + 1;
                ModUtils.drawTexturedRectBatched(x + 8, y + 41 - l, 176, 14 - l, 14, l, 1.0f / 256, buffer); // fire
            }
            int l = MathHelper.ceil(cookProgress * 24.0F);
            ModUtils.drawTexturedRectBatched(x + 32, y + 26, 178, 15, l, 16, 1.0f / 256, buffer); // progress bar
        }

        public static void renderBrewingStandProgress(BrewingStandBlockEntity brewingStand, int x, int y, BufferBuilder buffer) {
            PropertyDelegate propertyDelegate = ((Accessors.BrewingStandBlockEntityAccessor) brewingStand).inventory_preview_fix_getPropertyDelegate();
            int brewTime = propertyDelegate.get(0);
            int fuel = propertyDelegate.get(1);

            RenderUtils.bindTexture(TEXTURE_BREWING_STAND);
            int l = MathHelper.clamp((18 * fuel + 20 - 1) / 20, 0, 18);
            if (l > 0) {
                ModUtils.drawTexturedRectBatched(x + 51, y + 35, 176, 29, l, 4, 1.0f / 256, buffer);//fuel
            }

            final int[] BUBBLE_PROGRESS = new int[]{29, 24, 20, 16, 11, 6, 0};
            if (brewTime <= 0) return;

            int n = (int) (28.0F * (1.0F - brewTime / 400.0F));
            if (n > 0) {
                ModUtils.drawTexturedRectBatched(x + 90, y + 7, 179, 0, 5, n, 1.0f / 256, buffer);//brew bar
            }

            n = BUBBLE_PROGRESS[brewTime / 2 % 7];
            if (n > 0) {
                ModUtils.drawTexturedRectBatched(x + 55, y + 32 - n, 185, 27 - n, 12, n, 1.0f / 256, buffer);//bubble
            }
        }
    }
}
