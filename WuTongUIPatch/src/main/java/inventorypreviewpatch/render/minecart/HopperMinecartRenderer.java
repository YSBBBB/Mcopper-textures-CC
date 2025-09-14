package inventorypreviewpatch.render.minecart;

import fi.dy.masa.malilib.util.WorldUtils;
import inventorypreviewpatch.ModUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

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
        boolean enabled = hopperMinecartEntityRenderState.enabled;
        blockState = blockState.with(HopperBlock.ENABLED, enabled);
        super.renderBlock(hopperMinecartEntityRenderState, blockState, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public void updateRenderState(HopperMinecartEntity hopperMinecartEntity, HopperMinecartEntityRenderState hopperMinecartEntityRenderState, float f) {
        super.updateRenderState(hopperMinecartEntity, hopperMinecartEntityRenderState, f);
        //寻常方法不管用，使用malilib库
        if (RENDER_LOCKED_HOPPER_MINECART.getBooleanValue()) {
            World world = WorldUtils.getBestWorld(MinecraftClient.getInstance());
            NbtCompound nbt = new NbtCompound();
            Pair<Entity, NbtCompound> pair = ModUtils.getDataSyncer(null).requestEntity(world, hopperMinecartEntity.getId());

            if (pair != null) {
                nbt = pair.getRight();
            }
            hopperMinecartEntityRenderState.enabled = !nbt.contains("Enabled") || nbt.getBoolean("Enabled");
        } else {
            hopperMinecartEntityRenderState.enabled = true;
        }
    }
}


