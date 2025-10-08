package inventorypreviewpatch.render.minecart;

import fi.dy.masa.malilib.util.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.world.World;

import static inventorypreviewpatch.configs.Configs.Generic.RENDER_LOCKED_HOPPER_MINECART;

@Environment(EnvType.CLIENT)
public class HopperMinecartRenderer extends AbstractMinecartEntityRenderer<HopperMinecartEntity, HopperMinecartEntityRenderState> {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public HopperMinecartRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, EntityModelLayers.HOPPER_MINECART);
    }

    @Override
    public HopperMinecartEntityRenderState createRenderState() {
        return new HopperMinecartEntityRenderState();
    }

    @Override
    protected void renderBlock(HopperMinecartEntityRenderState renderState, BlockState blockState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        boolean enabled = renderState.rendering_enabled;
        blockState = blockState.with(HopperBlock.ENABLED, enabled);
        super.renderBlock(renderState, blockState, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public void updateRenderState(HopperMinecartEntity hopperMinecart, HopperMinecartEntityRenderState renderState, float f) {
        super.updateRenderState(hopperMinecart, renderState, f);
        World world = WorldUtils.getBestWorld(mc);
        //服务器无法在没有模组的情况下彻底支持(需要漏斗矿车重新经过激活铁轨进行更新)
        if (world instanceof ServerWorld) {
            renderState.shadowOfServer = (HopperMinecartEntity)world.getEntityById(hopperMinecart.getId());
            if (renderState.shadowOfServer != null) {
                renderState.logic_enabled = renderState.shadowOfServer.isEnabled();
            }
        } else {
            BlockState state = hopperMinecart.getWorld().getBlockState(hopperMinecart.getBlockPos());
            if (state.getBlock() == Blocks.ACTIVATOR_RAIL) {
                renderState.logic_enabled = !state.get(Properties.POWERED);
            }
        }
        renderState.rendering_enabled = !RENDER_LOCKED_HOPPER_MINECART.getBooleanValue() || renderState.logic_enabled;
    }
}
