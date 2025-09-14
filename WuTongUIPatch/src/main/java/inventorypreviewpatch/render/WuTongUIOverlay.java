package inventorypreviewpatch.render;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import inventorypreviewpatch.ModUtils;
import inventorypreviewpatch.WuTongUIPatch;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.*;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static fi.dy.masa.malilib.render.InventoryOverlay.*;
import static fi.dy.masa.malilib.render.RenderUtils.color;
import static fi.dy.masa.malilib.render.RenderUtils.getColorComponents;
import static inventorypreviewpatch.configs.Configs.Fixes.INVENTORY_PREVIEW_FIX_MODE;
import static inventorypreviewpatch.configs.Configs.Generic.DISPLAY_TRAPPED_CHEST_TITLE;
import static inventorypreviewpatch.event.ResourcesLoadedListener.*;
import static inventorypreviewpatch.render.WuTongUIOverlayHandler.renderFrame;

public class WuTongUIOverlay {

    private static final Identifier TEXTURE_BREWING_STAND = Identifier.ofVanilla("textures/gui/container/brewing_stand.png");
    private static final Identifier TEXTURE_FIXED_GENERIC_54 = Identifier.ofVanilla("textures/gui/container/fixed_generic_54.png");
    private static final Identifier TEXTURE_FIXED_GENERIC_27 = Identifier.ofVanilla("textures/gui/container/fixed_generic_54.png");
    private static final Identifier TEXTURE_ENDER_CHEST = Identifier.ofVanilla("textures/font/b111.png");
    private static final Identifier TEXTURE_GENERIC_54 = Identifier.ofVanilla("textures/gui/container/generic_54.png");
    private static final Identifier TEXTURE_BARREL = Identifier.ofVanilla("textures/font/b116.png");
    private static final Identifier TEXTURE_SHULKER_BOX_GRAY = Identifier.of("inventorypreviewpatch", "textures/gui/container/shulker_box_gray_sprite.png");
    private static final Identifier TEXTURE_SHULKER_BOX = Identifier.ofVanilla("textures/gui/container/shulker_box.png");
    private static final Identifier TEXTURE_FURNACE = Identifier.ofVanilla("textures/gui/container/furnace.png");
    private static final Identifier TEXTURE_SMOKER = Identifier.ofVanilla("textures/gui/container/smoker.png");
    private static final Identifier TEXTURE_BLAST_FURNACE = Identifier.ofVanilla("textures/gui/container/blast_furnace.png");

    public static <T> Text amendTitle(Screen screen, T containerEntity) {
        Text title = Text.translatable("inventorypreviewpatch.blank.title");
        if (!(screen instanceof HandledScreen<?> handledScreen)) return title;
        boolean isLargeInventory = handledScreen.getScreenHandler().slots.size() >= 54;
        boolean UseSpecificLanguage = isChinese || isEN_US;
        switch (containerEntity) {
            case null -> {
                if (screen instanceof AnvilScreen) {
                    title = Text.translatable("block.minecraft.anvil");
                } else if (screen instanceof SmithingScreen) {
                    title = Text.translatable("block.minecraft.smithing_table");
                }
            }
            case TrappedChestBlockEntity ignore -> {
                if (DISPLAY_TRAPPED_CHEST_TITLE.getBooleanValue()) {
                    if (UseSpecificLanguage && isLargeInventory) {
                        title = isChinese ? Text.of("大型陷阱箱子") : Text.of("Large Trapped Chest");
                    } else {
                        title = Text.translatable("block.minecraft.trapped_chest"); //要知道，陷阱箱子是没有独立标题的
                    }
                } else {
                    title = Text.translatable("container.chestDouble");
                }
            }
            case ChestBlockEntity ignore -> {
                if (UseSpecificLanguage) {
                    if (isLargeInventory) {
                        title = isChinese ? Text.of("大型箱子") : Text.of("Large Chest");
                    } else {
                        title = isChinese ? Text.of("箱子") : Text.of("Chest");
                    }
                } else {
                    title = Text.translatable("container.chest" + (isLargeInventory ? "Double" : null));
                }
            }
            case BarrelBlockEntity ignore -> {
                if (UseSpecificLanguage) {
                    title = isLargeInventory ? Text.translatable("container.large.barrel") : Text.translatable("container.small.barrel");
                } else {
                    title = Text.translatable("container.barrel");
                }
            }
            case ChestMinecartEntity ignore -> {
                if (UseSpecificLanguage) {
                    title = isChinese ? Text.of("运输矿车") : Text.of("Minecart with Chest");
                } else {
                    title = Text.translatable("entity.minecraft.chest_minecart");
                }
            }
            case HopperMinecartEntity ignore -> {
                if (UseSpecificLanguage) {
                    title = isChinese ? Text.of("漏斗矿车") : Text.of("Minecart with Hopper");
                } else {
                    title = Text.translatable("entity.minecraft.hopper_minecart");
                }
            }
            case AbstractBoatEntity boatEntity -> {
                if (boatEntity instanceof ChestRaftEntity) {
                    if (UseSpecificLanguage) {
                        title = isChinese ? Text.of("筏载箱子") : Text.of("Chest on Bamboo Raft");
                    } else {
                        title = Text.translatable("entity.minecraft.bamboo_chest_raft");
                    }
                } else if (boatEntity instanceof ChestBoatEntity) {
                    if (UseSpecificLanguage) {
                        title = isChinese ? Text.of("船载箱子") : Text.of("Chest on Boat");
                    }
                }
            }
            case EnderChestBlockEntity ignore -> {
                if (UseSpecificLanguage) {
                    title = isChinese ? Text.of("末影箱") : Text.of("Ender Chest");
                } else {
                    title = Text.translatable("container.enderchest");
                }
            }
            default -> {
                if (containerEntity instanceof BlockEntity be) {
                    Block block = be.getCachedState().getBlock();
                    title = block.getName() != null ? block.getName() : block.asItem().getName();
                } else if (containerEntity instanceof Entity entity) {
                    title = entity.getName();
                }
            }
        }
        return title;
    }

    public static class PreviewOverlay {
        private static final Identifier DATA_CORRECTION = Identifier.ofVanilla("textures/preview/overlay/data_correction.json");
        private static Map<String, int[]> render_correction = new HashMap<>();

        public static void setShulkerboxBackgroundTintColor(@Nullable ShulkerBoxBlock block, boolean useBgColors) {
            if (block != null && useBgColors) {
                switch (INVENTORY_PREVIEW_FIX_MODE.getStringValue()) {
                    case "vanilla", "no" -> {
                        final DyeColor dye = block.getColor() != null ? block.getColor() : DyeColor.PURPLE;
                        final float[] colors = getColorComponents(dye.getEntityColor());
                        color(colors[0], colors[1], colors[2], 1f);
                    }
                    case "wutong" -> {
                        if (isLoadedWuTongUI) {
                            //原图用材质包里的
                            final DyeColor dye = block.getColor();
                            System.out.println(dye != null ? dye.toString() : "primeval");
                            if (dye == null) {
                                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                                break;
                            }
                            //太黑会糊成一坨,单独给它调一个
                            float[] colors = dye == DyeColor.BLACK ? new float[]{0.15f, 0.15f, 0.15f} : getColorComponents(dye.getEntityColor());
                            RenderSystem.setShaderColor(colors[0], colors[1], colors[2], 1.0f);
                        }
                    }
                    case "customization" -> {
                        final DyeColor dye = block.getColor();
                        String colorId = "color_" + (dye != null ? dye.toString() : "primeval");
                        if (render_correction.containsKey(colorId)) {
                            int color = render_correction.get(colorId)[0];
                            int red = ColorHelper.getRed(color);
                            int green = ColorHelper.getGreen(color);
                            int blue = ColorHelper.getBlue(color);
                            int alpha = ColorHelper.getAlpha(color);
                            float[] colors = new float[]{red, green, blue, alpha};
                            RenderSystem.setShaderColor(colors[0], colors[1], colors[2], colors[3]);
                        } else {
                            final float[] colors = getColorComponents(dye != null ? dye.getEntityColor() : DyeColor.PURPLE.getEntityColor());
                            color(colors[0], colors[1], colors[2], 1f);
                        }
                    }
                }
            } else {
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            }
        }

        public static void loadCustomMaterialParameters(MinecraftClient mc) {
            if (mc.getResourceManager().getResource(DATA_CORRECTION).isEmpty()) {
                render_correction = new HashMap<>();
                return;
            }
            try {
                Reader reader = mc.getResourceManager().getResource(DATA_CORRECTION).get().getReader();
                JsonElement element = JsonParser.parseReader(reader);
                JsonObject object = element.getAsJsonObject();

                Map<String, int[]> map = new HashMap<>();
                if (object.has("data_correction")) {
                    JsonArray data_correction = object.get("data_correction").getAsJsonArray();
                    for (int i = 0; i < data_correction.size(); i++) {
                        JsonObject subObject = data_correction.get(i).getAsJsonObject();
                        if (subObject.has("id")) {
                            int width = subObject.has("width") ? subObject.get("width").getAsInt() : 256;
                            int height = subObject.has("height") ? subObject.get("height").getAsInt() : 256;
                            int x_offset = subObject.has("x_offset") ? subObject.get("x_offset").getAsInt() : 0;
                            int y_offset = subObject.has("y_offset") ? subObject.get("y_offset").getAsInt() : 0;
                            map.put(subObject.get("id").getAsString(), new int[]{width, height, x_offset, y_offset});
                        }
                    }
                }
                if (object.has("shulkerbox_color")) {
                    JsonArray shulkerboxColorCorrection = object.get("shulkerbox_color").getAsJsonArray();
                    for (int i = 0; i < shulkerboxColorCorrection.size(); i++) {
                        JsonObject subObject = shulkerboxColorCorrection.get(i).getAsJsonObject();
                        if (subObject.has("id")) {
                            int color = subObject.has("color") ? subObject.get("color").getAsInt() : 0xFFFFFFFF;
                            map.put(subObject.get("id").getAsString(), new int[]{color});
                        }
                    }
                }
                render_correction = map;
            } catch (IOException e) {
                WuTongUIPatch.LOGGER.error(String.valueOf(e));
            }
        }

        public static void renderInventoryBackground(InventoryRenderType type, int x, int y, int slotsPerRow, int totalSlots, Context previewData, MinecraftClient mc, DrawContext drawContext) {

            if (isLoadedWuTongUI && INVENTORY_PREVIEW_FIX_MODE.getStringValue().equals("wutong")) {
                renderFrame(type, previewData, x, y, slotsPerRow, totalSlots, 1);
                if (previewData.be() instanceof BarrelBlockEntity) {
                    renderFrame(type, previewData, x, y, slotsPerRow, totalSlots, 2);
                }
            } else if (previewData.nbt() != null && INVENTORY_PREVIEW_FIX_MODE.getStringValue().equals("customization")) {
                String id = previewData.nbt().getString("id").replace(':', '-');
                Identifier texture = Identifier.ofVanilla("textures/preview/overlay/" + id + ".png");
                if (mc.getResourceManager().getResource(texture).isEmpty()) {
                    InventoryOverlay.renderInventoryBackground(type, x, y, slotsPerRow, totalSlots, mc);
                } else {
                    int[] correction = {256, 256, 0, 0};
                    if (render_correction.containsKey(id)) {
                        correction = render_correction.get(id);
                    }
                    drawContext.drawTexture(RenderLayer::getGuiTextured, texture, correction[2], correction[3], 0.0F, 0.0F, 256, 256, correction[0], correction[1]);
                }
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
                boolean isntBlastFurnace = !(be instanceof BlastFurnaceBlockEntity);
                //根据熔炉类型设定材质
                Identifier FURNACE = switch (be) {
                    case SmokerBlockEntity ignore -> TEXTURE_SMOKER;
                    case BlastFurnaceBlockEntity ignore -> TEXTURE_BLAST_FURNACE;
                    default -> TEXTURE_FURNACE;
                };
                RenderUtils.bindTexture(FURNACE);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, isntBlastFurnace ? 47 : 56, 4, buffer); // top (left)
                RenderUtils.drawTexturedRectBatched(x + (isntBlastFurnace ? 47 : 56), y, 126, 0, 50, 4, buffer); // top (right）
                RenderUtils.drawTexturedRectBatched(x, y + 4, 0, 13, 4, 64, buffer); // left
                RenderUtils.drawTexturedRectBatched(x + (isntBlastFurnace ? 94 : 103), y + 4, 173, 13, 4, 64, buffer); // right
                RenderUtils.drawTexturedRectBatched(x + 4, y + 4, 52, 13, isntBlastFurnace ? 90 : 99, 64, buffer); // middle
            } else if (type == InventoryRenderType.BREWING_STAND) {
                RenderUtils.bindTexture(TEXTURE_BREWING_STAND);
                RenderUtils.drawTexturedRectBatched(x + 4, y - 9, 13, 0, 115, 74, buffer); // main
                RenderUtils.drawTexturedRectBatched(x + 63, y + 65, 72, 74, 30, 7, buffer); // addition
            } else if (type == InventoryRenderType.CRAFTER) {
                RenderUtils.bindTexture(TEXTURE_CRAFTER);
                RenderUtils.drawTexturedRectBatched(x - 5, y - 5, 13, 4, 78, 71, buffer); // middle
                RenderUtils.drawTexturedRectBatched(x - 5, y + 66, 13, 10, 78, 1, buffer); // addition
                RenderUtils.drawTexturedRectBatched(x - 5, y + 67, 13, 7, 78, 3, buffer); // addition
                RenderUtils.drawTexturedRectBatched(x - 5, y + 70, 13, 3, 78, 4, buffer); // addition
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
                RenderUtils.drawTexturedRectBatched(x - 16, y + 31, 0, 43, 140, 1, buffer); // main
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
                RenderUtils.bindTexture(TEXTURE_FIXED_GENERIC_27);
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

        public static void renderWuTongBarrelBackground(int x, int y, int totalSlots, BufferBuilder buffer) {
            boolean isSmallInventory = totalSlots == 27;
            RenderUtils.bindTexture(TEXTURE_GENERIC_54);
            ModUtils.drawTexturedRectBatched(x + 7, y + 7, 7, 17, 162, isSmallInventory ? 54 : 108, 1.0f / 256, buffer); // middle
        }

        public static void renderFurnaceProgress(AbstractFurnaceBlockEntity furnace, int x, int y, BufferBuilder buffer) {
            boolean isBurning = furnace.propertyDelegate.get(0) > 0;
            int i = furnace.propertyDelegate.get(2);
            int j = furnace.propertyDelegate.get(3);
            int k = furnace.propertyDelegate.get(1);
            if (k == 0) {
                k = 200;
            }
            float cookProgress = j != 0 && i != 0 ? MathHelper.clamp((float) i / j, 0.0F, 1.0F) : 0.0f;
            float fuelProgress = MathHelper.clamp((float) furnace.propertyDelegate.get(0) / k, 0.0F, 1.0F);

            RenderUtils.bindTexture(TEXTURE_FURNACE);
            if (isBurning) {
                int l = MathHelper.ceil(fuelProgress * 13.0F) + 1;
                ModUtils.drawTexturedRectBatched(x + 8, y + 41 - l, 176, 14 - l, 14, l, 1.0f / 256, buffer); // fire
            }
            int l = MathHelper.ceil(cookProgress * 24.0F);
            ModUtils.drawTexturedRectBatched(x + 32, y + 26, 178, 15, l, 16, 1.0f / 256, buffer); // progress bar
        }

        public static void renderBrewingStandProgress(BrewingStandBlockEntity brewingStand, int x, int y, BufferBuilder buffer) {
            int brewTime = brewingStand.propertyDelegate.get(0);
            int fuel = brewingStand.propertyDelegate.get(1);

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
