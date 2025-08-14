package inventorypreviewpatch.render;


import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import inventorypreviewpatch.ModUtils;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

import static fi.dy.masa.malilib.render.InventoryOverlay.*;
import static inventorypreviewpatch.configs.Configs.Fixes.Render_Shulkerbox_Background;
import static net.minecraft.screen.ScreenHandlerType.GENERIC_9X6;

public class WuTongUIOverlay {

    public static void setShulkerboxBackgroundTintColor(@Nullable ShulkerBoxBlock block, boolean useBgColors) {
            if (block != null && useBgColors && Render_Shulkerbox_Background.getBooleanValue())
            {
                //原图就是这个颜色
                DyeColor dyeColor = block.getColor();
                if (dyeColor == null) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    return;
                }

                final DyeColor dye =  block.getColor();
                float[] colors = RenderUtils.getColorComponents(dye.getEntityColor());
                final float[] basecolors = RenderUtils.getColorComponents(0xFF9174);
                //黑色，灰色，淡灰色，棕色白色出现了偏紫的现象，需要微调
                if (dyeColor == DyeColor.BLACK || dyeColor == DyeColor.GRAY || dyeColor == DyeColor.LIGHT_GRAY || dyeColor == DyeColor.WHITE || dyeColor == DyeColor.BROWN) {
                    final float r ; final float g ; final float b ;//设置权重
                    if (dyeColor == DyeColor.BLACK) { r = 1.000f ; g = 1.7586f ; b = 2.1983f ;}
                    else if (dyeColor == DyeColor.GRAY) { r = 1.5020f ; g = 1.8828f ; b = 2.1034f ;}
                    else if (dyeColor == DyeColor.LIGHT_GRAY) { r = 1.8549f ; g = 2.5034f ; b = 2.8793f ;}
                    //else if (dyeColor == DyeColor.BROWN ) { r = 0.5451f ; g = 0.4759f ; b = 0.1638f;}
                    else if (dyeColor == DyeColor.BROWN ) { r = colors[0]*colors[0]/basecolors[0] ; g = colors[1]*colors[1]/basecolors[1] ; b = colors[2]*colors[2]/basecolors[2] ;}
                    else { r = 1.000f ; g = 1.7586f ; b = 2.1983f ;}
                    RenderSystem.setShaderColor(basecolors[0] * r, basecolors[1] * g, basecolors[2] * b, 1.0f);

                    //RenderSystem.setShaderColor(colors[0] * r /basecolors[0], colors[1] * g /basecolors[1], colors[2] *b /basecolors[2], 1.0f);
                    return;
                }
                RenderSystem.setShaderColor(colors[0], colors[1], colors[2], 1.0f);
            }
            else
            {
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            }
        }

    public static void renderInventoryBackground(InventoryRenderType type, int x, int y, int slotsPerRow, int totalSlots, MinecraftClient mc) {
            RenderUtils.setupBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            BuiltBuffer builtBuffer;

            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);

            if (type == InventoryRenderType.BREWING_STAND) {
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
                RenderUtils.drawTexturedRectBatched(x + 61, y + 7,152, 15, 7, 54, buffer); // right
                RenderUtils.drawTexturedRectBatched(x, y, 17, 0, 34, 7, buffer); // top (left)
                RenderUtils.drawTexturedRectBatched(x + 34, y, 125, 0, 34, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 61, 17, 70, 34, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 34, y + 61, 125, 70, 34, 7, buffer); // bottom (right)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 61, 16, 54, 54, buffer); // middle
            } else if (type == InventoryRenderType.HOPPER) {
                RenderUtils.bindTexture(TEXTURE_HOPPER);
                RenderUtils.drawTexturedRectBatched(x - 36, y - 12, 0, 0, 167, 43, buffer); // main
                RenderUtils.drawTexturedRectBatched(x - 16, y + 31, 0, 43, 140, 1, buffer); // main
            }
            // Most likely a Villager, or possibly a Llama
            else if (type == InventoryRenderType.VILLAGER) {
                //Use fixed texture
                RenderUtils.bindTexture(Identifier.ofVanilla("textures/gui/container/fixed_generic_54.png"));
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, 79, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 133, 0, 43, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 79, 0, 215, 43, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 43, y + 7, 169, 143, 7, 79, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 7, 17, 36, 72, buffer); // 2x4 slots*/
            } else if (type == InventoryRenderType.FIXED_27) {
                //Use fixed texture
                RenderUtils.bindTexture(Identifier.ofVanilla("textures/gui/container/fixed_shulker_box.png"));
                RenderUtils.drawTexturedRectBatched(x      , y     ,   0,   0,   7,  61, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x +   7, y     ,   7,   0, 169,   7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x      , y + 61,   0, 159, 169,   7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 169, y +  7, 169, 105,   7,  61, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x +   7, y +  7,   7,  17, 162,  54, buffer); // middle
            } else if (type == InventoryRenderType.FIXED_54) {
                //Use fixed texture
                RenderUtils.bindTexture(Identifier.ofVanilla("textures/gui/container/fixed_generic_54.png"));
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, 7, 115, buffer); // left (top)
                RenderUtils.drawTexturedRectBatched(x + 7, y, 7, 0, 169, 7, buffer); // top (right)
                RenderUtils.drawTexturedRectBatched(x, y + 115, 0, 215, 169, 7, buffer); // bottom (left)
                RenderUtils.drawTexturedRectBatched(x + 169, y + 7, 169, 107, 7, 115, buffer); // right (bottom)
                RenderUtils.drawTexturedRectBatched(x + 7, y + 7, 7, 17, 162, 108, buffer); // middle
            } else if (type == InventoryRenderType.ENDER_CHEST) {
                final Identifier TEXTURE_ENDER_CHEST    = Identifier.ofVanilla("textures/font/b111.png");
                RenderUtils.bindTexture(TEXTURE_ENDER_CHEST);
                ModUtils.drawTexturedRectBatched(x, y - 10, 0,   0, 176, 80, 1.0f/176, buffer); // main
                ModUtils.drawTexturedRectBatched(x + 85, y +  70, 85, 80,   6,  2, 1.0f/176,buffer); // latch
            } else {
                //Use fixed texture
                RenderUtils.bindTexture(Identifier.ofVanilla("textures/gui/container/fixed_generic_54.png"));

                // Draw the slot backgrounds according to how many slots there actually are
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

                    // Render the background for the last non-existing slots on the last row,
                    // in two strips of the background texture from the double chest texture's top part.
                    if (rows > 1 && rowLen < slotsPerRow) {
                        RenderUtils.drawTexturedRectBatched(x + rowLen * 18 + 7, y + row * 18 + 7, 7, 3, (slotsPerRow - rowLen) * 18, 9, buffer);
                        RenderUtils.drawTexturedRectBatched(x + rowLen * 18 + 7, y + row * 18 + 16, 7, 3, (slotsPerRow - rowLen) * 18, 9, buffer);
                    }
                }
            }

            //RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();

            try {
                builtBuffer = buffer.end();
                BufferRenderer.drawWithGlobalProgram(builtBuffer);
                builtBuffer.close();
            } catch (Exception ignored) {}
    }

    public static void renderSpecialInventoryBackground(BlockEntity be, int x, int y, int totalSlots) {
            RenderUtils.setupBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            BuiltBuffer builtBuffer;

            boolean isSmallInventory = totalSlots <= 27;
            final Identifier TEXTURE_CHEST = Identifier.ofVanilla("textures/gui/container/generic_54.png");
            final Identifier TEXTURE_BARREL = Identifier.ofVanilla("textures/font/b116.png");
            final Identifier TEXTURE_SHULKER_BOX     = Identifier.ofVanilla("textures/gui/container/shulker_box.png");
            Identifier TEXTURE_FURNACE = Identifier.ofVanilla("textures/gui/container/furnace.png");

            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);

            if (be instanceof ShulkerBoxBlockEntity && Render_Shulkerbox_Background.getBooleanValue()) {
                RenderUtils.bindTexture(TEXTURE_SHULKER_BOX);
                ModUtils.drawTexturedRectBatched(x      , y -  9,   0, 0, 176, 16,1.0f/256, buffer); // head
                ModUtils.drawTexturedRectBatched(x      , y + 7,   0, 17, 176, 54,1.0f/256, buffer); // middle
                ModUtils.drawTexturedRectBatched(x      , y + 61,   0, 71, 176,   5,1.0f/256, buffer); // bottom
                ModUtils.drawTexturedRectBatched(x      , y + 66,   0, 74, 176,   2,1.0f/256, buffer); // bottom2
            }
            else if (be instanceof ChestBlockEntity) {
                //绘制（大）箱子的材质
                RenderUtils.bindTexture(TEXTURE_CHEST);
                ModUtils.drawTexturedRectBatched(x      , y - (isSmallInventory? 9 : 0),   0, 0, 176,   isSmallInventory? 16 : 7,1.0f/256, buffer); // head
                ModUtils.drawTexturedRectBatched(x      , y + 7,   0, 17, 176,   isSmallInventory? 54 : 108,1.0f/256, buffer); // middle
                ModUtils.drawTexturedRectBatched(x      , y + (isSmallInventory? 61 : 115),   0, 126, 176,   8,1.0f/256, buffer); // bottom
                ModUtils.drawTexturedRectBatched(x + 85, y + (isSmallInventory? 69 : 123), 85, 134,   6,  2, 1.0f/256, buffer); // latch
            }
            else if (be instanceof BarrelBlockEntity){
                //绘制（大）木桶的材质
                RenderUtils.bindTexture(TEXTURE_BARREL);
                if (isSmallInventory) {
                    ModUtils.drawTexturedRectBatched(x, y - 10, 0, 0, 176, 81, 1.0f / 176, buffer);
                } else {
                    ModUtils.drawTexturedRectBatched(x      , y  ,   0, 0, 176,7,1.0f/176, buffer); // head
                    ModUtils.drawTexturedRectBatched(x      , y + 7,   0, 18, 176, 53,1.0f/176, buffer); // middle
                    ModUtils.drawTexturedRectBatched(x, y + 60,   0, 18, 176, 2,1.0f/176, buffer); // middle
                    ModUtils.drawTexturedRectBatched(x      , y + 62,   0, 18, 176,   61,1.0f/176, buffer); // bottom
                }
            }
            else if (be instanceof AbstractFurnaceBlockEntity) {
                boolean isntBlastFurnace = ! (be instanceof BlastFurnaceBlockEntity);
                //根据熔炉类型重新设定材质
                if (be instanceof SmokerBlockEntity) TEXTURE_FURNACE = Identifier.ofVanilla("textures/gui/container/smoker.png");
                else if (be instanceof BlastFurnaceBlockEntity)  TEXTURE_FURNACE= Identifier.ofVanilla("textures/gui/container/blast_furnace.png");
                RenderUtils.bindTexture(TEXTURE_FURNACE);
                RenderUtils.drawTexturedRectBatched(x, y, 0, 0, isntBlastFurnace? 47 :56, 4, buffer); // top (left)
                RenderUtils.drawTexturedRectBatched(x + (isntBlastFurnace? 47 :56), y, 126, 0, 50, 4, buffer); // top (right）
                RenderUtils.drawTexturedRectBatched(x, y + 4, 0, 13, 4, 64, buffer); // left
                RenderUtils.drawTexturedRectBatched(x + (isntBlastFurnace? 94 : 103), y + 4, 173, 13, 4, 64, buffer); // right
                RenderUtils.drawTexturedRectBatched(x + 4, y + 4, 52, 13, isntBlastFurnace? 90 : 99, 64, buffer); // middle
            }

            RenderSystem.enableBlend();
            try {
                builtBuffer = buffer.end();
                BufferRenderer.drawWithGlobalProgram(builtBuffer);
                builtBuffer.close();
            } catch (Exception ignored) {
            }
    }

    public static void renderBarrelOverlay(ScreenHandlerType<?> type) {
        if (type == null) return;
        RenderUtils.setupBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        BuiltBuffer builtBuffer;

        final Identifier TEXTURE_BARREL = Identifier.ofVanilla("textures/font/b116.png");

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);

        final int xCenter = GuiUtils.getScaledWindowWidth() / 2;
        final int yCenter = GuiUtils.getScaledWindowHeight() / 2;
        int totalSlots = 54;
        int rows = (int) (Math.ceil((double) totalSlots / (double) 9));
        int width = 9 * 18 + 14;
        int height = rows * 18 + 14;
        int x = xCenter - (width / 2);
        int y = yCenter - height - 6;

        //绘制（大）木桶的材质
        RenderUtils.bindTexture(TEXTURE_BARREL);
        if (type == GENERIC_9X6) {
            ModUtils.drawTexturedRectBatched(x, y + 17, 0, 0, 176, 18, 1.0f / 176, buffer); // head
            ModUtils.drawTexturedRectBatched(x, y + 35, 0, 18, 176, 53, 1.0f / 176, buffer); // middle
            ModUtils.drawTexturedRectBatched(x, y + 88, 0, 18, 176, 1, 1.0f / 176, buffer); // middle
            ModUtils.drawTexturedRectBatched(x, y + 89, 0, 18, 176, 66, 1.0f / 176, buffer); // bottom
        } else {
            ModUtils.drawTexturedRectBatched(x, y + 44, 0, 0, 176, 85, 1.0f / 176, buffer); //main
        }

        RenderSystem.enableBlend();
        try {
            builtBuffer = buffer.end();
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
            builtBuffer.close();
        } catch (Exception ignored) {
        }
    }

    public static void renderFurnaceProgress( Identifier texture, int x, int y ,float cookProgress , float fuelProgress, boolean isBurning) {
        RenderUtils.setupBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        BuiltBuffer builtBuffer;

        RenderUtils.bindTexture(texture);
        if (isBurning) {
            int l = MathHelper.ceil(fuelProgress * 13.0F) + 1;
            ModUtils.drawTexturedRectBatched(x + 8, y + 41 - l, 176, 14 - l, 14, l, 1.0f / 256, buffer); // fire
        }
        int l = MathHelper.ceil(cookProgress * 24.0F);
        ModUtils.drawTexturedRectBatched(x + 32, y + 26, 178, 15, l, 16, 1.0f / 256, buffer); // progress bar

        RenderSystem.enableBlend();
        try {
            builtBuffer = buffer.end();
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
            builtBuffer.close();
        } catch (Exception ignored) {
        }
    }

    public static void renderBrewingStandProgress(Identifier texture, int x, int y, int brewTime, int fuel) {
        RenderUtils.setupBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        BuiltBuffer builtBuffer;

        RenderUtils.bindTexture(texture);

        int l = MathHelper.clamp((18 * fuel + 20 - 1) / 20, 0, 18);
        if (l > 0) {
            ModUtils.drawTexturedRectBatched(x + 51, y + 35, 176, 29, l, 4, 1.0f / 256, buffer);//fuel
        }

        final int[] BUBBLE_PROGRESS = new int[]{29, 24, 20, 16, 11, 6, 0};
        if (brewTime > 0) {
            int n = (int)(28.0F * (1.0F - brewTime / 400.0F));
            if (n > 0) {
                ModUtils.drawTexturedRectBatched(x + 90, y + 7, 179, 0, 5, n, 1.0f / 256, buffer);//brew bar
            }

            n = BUBBLE_PROGRESS[brewTime / 2 % 7];
            if (n > 0) {
                ModUtils.drawTexturedRectBatched(x + 55, y + 32 - n, 185, 27 - n, 12, n, 1.0f / 256, buffer);//bubble
            }
        }

        RenderSystem.enableBlend();
        try {
            builtBuffer = buffer.end();
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
            builtBuffer.close();
        } catch (Exception ignored) {
        }
    }
}