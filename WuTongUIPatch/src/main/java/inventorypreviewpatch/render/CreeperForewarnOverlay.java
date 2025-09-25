package inventorypreviewpatch.render;

import inventorypreviewpatch.ModUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import static inventorypreviewpatch.configs.Configs.Generic.CREEPER_FOREWARN;

@Environment(EnvType.CLIENT)
public class CreeperForewarnOverlay {
    private static final Identifier TEXTURE_CREEPER = Identifier.ofVanilla("textures/entity/creeper/creeper.png");
    private static final Identifier TEXTURE_CREEPER_CHARGED = Identifier.ofVanilla("textures/entity/creeper/creeper_armor.png");
    private static final RandomGenerator generator = RandomGeneratorFactory.of("L32X64MixRandom").create();
    private static final EntityType<CreeperEntity> CREEPER_TYPE = EntityType.CREEPER;
    private static boolean Forewarn = false;//初级预警
    private static boolean Emergency = false;//危险预警
    private static boolean IsCharged = false;//高压预警
    private static boolean AboutToExplode = false;//爆炸预警
    private static boolean IsFlash = false; //渲染闪动块
    private static final int[] charged_uv = {0,0};//高压头盔贴图的uv坐标
    private static int ticks = 0;//世界总刻数

    public static void updateState(MinecraftClient mc, float partialTicks) {
        if (!CREEPER_FOREWARN.getBooleanValue()) return;
        PlayerEntity player = mc.player;
        World world = mc.world;
        if (player == null || world == null) return;
        ticks++;
        //如果检测到苦力怕，则提高检查频率
        if (Forewarn ? ticks % 4 == 0 : ticks % 8 == 0) {
            Box largeBox = new Box(player.getBlockPos()).expand(16);
            List<CreeperEntity> allCreeper = world.getEntitiesByType(CREEPER_TYPE, largeBox, EntityPredicates.VALID_ENTITY);
            Forewarn = !allCreeper.isEmpty();
            if (!Forewarn) return;
            //更新苦力怕状态
            //直接赋值容易受到其他苦力怕影响,计数器赋值更加灵活
            int counter_Emergency = 0;
            int counter_ToExplode = 0;
            int counter_IsCharged = 0;
            for (CreeperEntity creeper : allCreeper) {
                if (creeper.distanceTo(player) <= 8) counter_Emergency++;
                if (creeper.isCharged()) counter_IsCharged++;
                //更快速准确的报警
                float f = creeper.getClientFuseTime(partialTicks);
                if (((f * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(f, 0.5F, 1.0F)) > 0) {
                    counter_ToExplode++;
                }
            }
            Emergency = counter_Emergency > 0 || allCreeper.size() > 3;
            AboutToExplode = counter_ToExplode > 0;
            IsCharged = counter_IsCharged > 0;
        }
        //更新渲染状态
        if (AboutToExplode) {
            if (ticks % 3 == 0) IsFlash = !IsFlash;
        } else {
            IsFlash = false;
        }
        if (IsCharged && ticks % 2 == 0) {
            //用随机数生成弄个动态的高压背景
            charged_uv[0] = generator.nextInt(128);
            charged_uv[1] = generator.nextInt(64);
        }
    }

    private static void renderCreeperForewarn(DrawContext context, int x, int y) {
        if (IsFlash) {
            context.fill(x - 8, y - 80, x + 8, y - 64, 0xFFFFFFFF);
        } else {
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_CREEPER, x - 8, y - 80, 16, 16, 16, 16, 128, 64, Emergency ? 0xFFFF0000 : 0xFFFFFFFF);
        }
        if (IsCharged) {
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_CREEPER_CHARGED, x - 11, y - 83, charged_uv[0], charged_uv[1], 22, 22, 128, 64);
        }
    }

    public static void renderCreeperForewarn(Screen screen, DrawContext context, int x, int y, MinecraftClient mc) {
        if (!CREEPER_FOREWARN.getBooleanValue() || !Forewarn) return;
        if (screen == null || screen instanceof GameMenuScreen) {
            if (ModUtils.isOnPreview(mc)) {
                y += 100;
            }
            renderCreeperForewarn(context, x, y);
        } else if (ModUtils.isContainerScreen(screen) || screen instanceof InventoryScreen) {
            if (screen instanceof GenericContainerScreen screen2) {
                int rows = screen2.getScreenHandler().getRows();
                int backgroundHeight = 114 + rows * 18;
                y = (screen.getNavigationFocus().height() - (backgroundHeight)) / 2 + 73;
            } else {
                y -= 35;
            }
            renderCreeperForewarn(context, x, y);
        }
    }
}