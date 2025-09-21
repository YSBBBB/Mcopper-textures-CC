package inventorypreviewpatch.render;

import fi.dy.masa.malilib.util.WorldUtils;
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
import net.minecraft.world.World;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import static inventorypreviewpatch.configs.Configs.Generic.CREEPER_FOREWARN;

@Environment(EnvType.CLIENT)
public class CreeperForewarnOverlay {
    private static final Identifier TEXTURE_CREEPER = Identifier.ofVanilla("textures/entity/creeper/creeper.png");
    private static final Identifier TEXTURE_CREEPER_CHARGED = Identifier.ofVanilla("textures/entity/creeper/creeper_armor.png");
    private static final Identifier TEXTURE_CREEPER_EXPLODE = Identifier.of("inventorypreviewpatch", "textures/entity/creeper/creeper_explode.png");
    private static final RandomGenerator generator = RandomGeneratorFactory.of("L32X64MixRandom").create();
    private static final EntityType<CreeperEntity> CREEPER_TYPE = EntityType.CREEPER;
    private static boolean Forewarn = false;//初级预警
    private static boolean Emergency = false;//危险预警
    private static boolean IsCharged = false;//高压预警
    private static boolean AboutToExplode = false;//爆炸预警
    private static boolean IsFlash = false; //渲染闪动块
    private static int charged_u = 0;//高压头盔贴图的u坐标
    private static int charged_v = 0;//高压头盔贴图的v坐标
    private static int ticks = 0;//世界总刻数

    public static void updateState(MinecraftClient mc, float partialTicks) {
        if (!CREEPER_FOREWARN.getBooleanValue()) return;
        PlayerEntity player = mc.player;
        World world = WorldUtils.getBestWorld(mc);
        if (player == null || world == null) return;
        ticks++;
        //如果检测到苦力怕，则提高检查频率
        if (Forewarn ? ticks % 4 == 0 : ticks % 8 == 0) {
            Box largeBox = new Box(player.getBlockPos()).expand(16);
            List<CreeperEntity> allCreeper = List.copyOf(world.getEntitiesByType(CREEPER_TYPE, largeBox, EntityPredicates.VALID_ENTITY));
            Forewarn = !allCreeper.isEmpty();
            if (!Forewarn) return;
            //更新苦力怕状态
            //直接赋值容易受到其他苦力怕影响,计数器赋值更加灵活
            int counter_Emergency = 0;
            int counter_ToExplode = 0;
            int counter_IsCharged = 0;
            for (CreeperEntity creeper : allCreeper) {
                //系数大可以让预警更及时
                if (creeper.distanceTo(player) <= 8) {
                    counter_Emergency++;
                }
                if (creeper.getClientFuseTime(partialTicks) * 30 > 6) {
                    counter_ToExplode++;
                }
                if (creeper.isCharged()) {
                    counter_IsCharged++;
                }
            }
            AboutToExplode = counter_ToExplode > 0;
            Emergency = counter_Emergency > 0 || allCreeper.size() > 3;
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
            charged_u = generator.nextInt(128);
            charged_v = generator.nextInt(64);
        }
    }
    private static void renderCreeperForewarn(DrawContext context, int x, int y, MinecraftClient mc) {
        PlayerEntity player = mc.player;
        World world = WorldUtils.getBestWorld(mc);
        if (player == null || world == null || !Forewarn) return;
        if (Emergency) {
            context.drawTexture(RenderLayer::getGuiTextured, IsFlash ? TEXTURE_CREEPER_EXPLODE : TEXTURE_CREEPER, x - 8, y - 80, 16, 16, 16, 16, 128, 64, IsFlash ? 0xFFFFFFFF : 0xFFFF0000);
        } else {
            context.drawTexture(RenderLayer::getGuiTextured, IsFlash ? TEXTURE_CREEPER_EXPLODE : TEXTURE_CREEPER, x - 8, y - 80, 16, 16, 16, 16, 128, 64);
        }
        if (IsCharged) {
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_CREEPER_CHARGED, x - 11, y - 83, charged_u, charged_v, 22, 22, 128, 64);
        }
    }

    public static void renderCreeperForewarn(Screen screen, DrawContext context, int x, int y, MinecraftClient mc) {
        if (!CREEPER_FOREWARN.getBooleanValue()) return;
        if (screen == null || screen instanceof GameMenuScreen) {
            //根据不同的帧数区间来设置超时时间，防止渣机错认为依然处在预览状态
            if (ModUtils.isOnPreview(mc)) {
                y += 100;
            }
            renderCreeperForewarn(context, x, y, mc);
        } else if (ModUtils.isContainerScreen(screen) || screen instanceof InventoryScreen) {
            if (screen instanceof GenericContainerScreen screen2) {
                int rows = screen2.getScreenHandler().getRows();
                int backgroundHeight = 114 + rows * 18;
                y = (screen.getNavigationFocus().height() - (backgroundHeight)) / 2 + 73;
            } else {
                y -= 35;
            }
            renderCreeperForewarn(context, x, y, mc);
        }
    }
}