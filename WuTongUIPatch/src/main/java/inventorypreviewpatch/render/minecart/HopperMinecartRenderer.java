package inventorypreviewpatch.render.minecart;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.state.property.Properties;

import static inventorypreviewpatch.configs.Configs.Generic.RENDER_LOCKED_HOPPER_MINECART;

@Environment(EnvType.CLIENT)
public class HopperMinecartRenderer extends AbstractMinecartEntityRenderer<HopperMinecartEntity, HopperMinecartEntityRenderState> {
    public HopperMinecartRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, EntityModelLayers.HOPPER_MINECART);
    }

    @Override
    public HopperMinecartEntityRenderState createRenderState() {
        return new HopperMinecartEntityRenderState();
    }

    @Override
    protected void renderBlock(HopperMinecartEntityRenderState hopperMinecartEntityRenderState, BlockState blockState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        boolean enabled = hopperMinecartEntityRenderState.rendering_enabled;
        blockState = blockState.with(HopperBlock.ENABLED, enabled);
        super.renderBlock(hopperMinecartEntityRenderState, blockState, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public void updateRenderState(HopperMinecartEntity hopperMinecartEntity, HopperMinecartEntityRenderState hopperMinecartEntityRenderState, float f) {
        super.updateRenderState(hopperMinecartEntity, hopperMinecartEntityRenderState, f);
        BlockState state = hopperMinecartEntity.getWorld().getBlockState(hopperMinecartEntity.getBlockPos());
        if (state.getBlock() == Blocks.ACTIVATOR_RAIL) {
            hopperMinecartEntityRenderState.logic_enabled = !state.get(Properties.POWERED);
        }
        hopperMinecartEntityRenderState.rendering_enabled = !RENDER_LOCKED_HOPPER_MINECART.getBooleanValue() || hopperMinecartEntityRenderState.logic_enabled;
    }
}


