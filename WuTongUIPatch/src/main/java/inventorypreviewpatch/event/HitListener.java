package inventorypreviewpatch.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HitListener {

    private static final HitListener INSTANCE = new HitListener();
    public LivingEntity player;
    public BlockPos pos;
    public Hand hand;
    public BlockEntity blockEntity;
    public Entity entity;
    public World world;

    public static HitListener getInstance() {
        return INSTANCE;
    }

    public void getHitBlockResult() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            this.player = player;
            this.pos = hitResult.getBlockPos();
            this.hand = hand;
            this.world = world;
            this.blockEntity = world.getBlockEntity(pos);
            //清除entity的缓存
            this.entity = null;
            return ActionResult.PASS;
        });
    }

    public void getHitEntityResult() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            this.player = player;
            this.pos = entity.getBlockPos();
            this.hand = hand;
            this.world = world;
            //清空blockEntity的缓存
            this.blockEntity = null;
            this.entity = entity;
            //System.out.println(Objects.requireNonNull(ModUtils.getDataSyncer(null).requestEntity(world, entity.getId())).getRight());
            return ActionResult.PASS;
        });
    }
}