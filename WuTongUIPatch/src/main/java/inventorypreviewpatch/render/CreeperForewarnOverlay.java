package inventorypreviewpatch.render;

import fi.dy.masa.malilib.util.WorldUtils;
import inventorypreviewpatch.ModUtils;
import inventorypreviewpatch.helper.MethodExecuteHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import static inventorypreviewpatch.configs.Configs.Generic.Creeper_Forewarn;

@Environment(EnvType.CLIENT)
public class CreeperForewarnOverlay {
//用读写线程分离的思路，仅单线程写入，不会引发并发修改异常

    private static final Identifier TEXTURE_CREEPER = Identifier.ofVanilla("textures/entity/creeper/creeper.png");
    private static final Identifier TEXTURE_CREEPER_CHARGED = Identifier.ofVanilla("textures/entity/creeper/creeper_armor.png");
    private static final Identifier TEXTURE_CREEPER_EXPLODE = Identifier.of("inventorypreviewpatch", "textures/entity/creeper/creeper_explode.png");
    private static boolean Forewarn = false;//初级预警
    private static boolean Emergency = false;//危险预警
    private static boolean AboutToExplode = false;//爆炸预警
    private static boolean IsCharged = false;//高压预警
    private static boolean IsFlash = false; //渲染闪动块
    private static int charged_u = 0;//高压头盔贴图的u坐标
    private static int charged_v = 0;//高压头盔贴图的v坐标
    private static long ticks;//世界总刻数

    public static void updateState(MinecraftClient mc, float partialTicks) {
        if (!Creeper_Forewarn.getBooleanValue()) return;
        PlayerEntity player = mc.player;
        World world = WorldUtils.getBestWorld(mc);
        if (player != null && world != null) {
            if (ticks != world.getTime() % 65535) {
                ticks = world.getTime() % 65535;
            }

            //如果检测到苦力怕，则提高检查频率
            if (Forewarn ? ticks % 2 == 0 : ticks % 4 == 0) {
                Box largeBox = new Box(player.getBlockPos()).expand(16);
                List<CreeperEntity> allCreeper = world.getEntitiesByClass(CreeperEntity.class, largeBox, EntityPredicates.VALID_ENTITY);

                Forewarn = !allCreeper.isEmpty();

                if (Forewarn) {
                    Box smallBox = new Box(player.getBlockPos()).expand(8);
                    List<CreeperEntity> lessCreeper = world.getEntitiesByClass(CreeperEntity.class, smallBox, EntityPredicates.VALID_ENTITY);
                    Emergency = !lessCreeper.isEmpty();
                    //直接赋值容易受到其他苦力怕影响,计数器赋值更加灵活
                    int counter_ToExplode = 0;
                    int counter_IsCharged = 0;
                    for (CreeperEntity creeper : allCreeper) {
                        //系数大可以让预警更及时
                        if (creeper.getClientFuseTime(partialTicks) * 30 > 5.5) {
                            counter_ToExplode++;
                        }
                        if (creeper.isCharged()) {
                            counter_IsCharged++;
                        }
                    }
                    AboutToExplode = counter_ToExplode > 0;
                    IsCharged = counter_IsCharged > 0;
                } else {
                    AboutToExplode = false;
                    IsCharged = false;
                }
            }

            if (Forewarn) {
                if (AboutToExplode) {
                    if (ticks % 4 == 0) {
                        IsFlash = !IsFlash;
                    }
                } else {
                    IsFlash = false;
                }

                if (IsCharged) {
                    //用随机数生成弄个动态的高压背景
                    if (ticks % 2 == 0) {
                        RandomGenerator generator = RandomGeneratorFactory.of("L32X64MixRandom").create();
                        charged_u = generator.nextInt(128);
                        charged_v = generator.nextInt(64);
                    }
                }
            }
        }
    }

    private static void renderCreeperForewarn(DrawContext context, int x, int y, MinecraftClient mc) {

        PlayerEntity player = mc.player;
        World world = WorldUtils.getBestWorld(mc);
        if (player != null && world != null) {
            if (Forewarn) {
                if (Emergency) {
                    context.drawTexture(RenderLayer::getGuiTextured, IsFlash ? TEXTURE_CREEPER_EXPLODE : TEXTURE_CREEPER, x - 8, y - 80, 16, 16, 16, 16, 128, 64, IsFlash ? 0xFFFFFFFF : 0xFFFF0000);
                } else {
                    context.drawTexture(RenderLayer::getGuiTextured, IsFlash ? TEXTURE_CREEPER_EXPLODE : TEXTURE_CREEPER, x - 8, y - 80, 16, 16, 16, 16, 128, 64);
                }
                if (IsCharged) {
                    context.drawTexture(RenderLayer::getGuiTextured, TEXTURE_CREEPER_CHARGED, x - 11, y - 83, charged_u, charged_v, 22, 22, 128, 64);
                }
            }
        }
    }

    public static void renderCreeperForewarn(Screen screen, DrawContext context, int x, int y, MinecraftClient mc) {
        if (!Creeper_Forewarn.getBooleanValue()) return;
        if (screen == null || screen instanceof GameMenuScreen) {
            if (MethodExecuteHelper.getExecutionState("inventory_preview")) {
                y += 100;
            }
            renderCreeperForewarn(context, x, y, mc);
        } else if (ModUtils.isGenericScreen(screen)) {
            if (screen instanceof GenericContainerScreen screen2) {
                int rows =  screen2.getScreenHandler().getRows();
                int backgroundHeight = 114 + rows * 18;
                y = (screen.getNavigationFocus().height() - (backgroundHeight)) / 2 + 73;
            } else {
                y -= 35;
            }
            renderCreeperForewarn(context, x, y, mc);
        }
    }
}