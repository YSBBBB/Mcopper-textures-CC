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

    public static HitListener getInstance() {
        return INSTANCE;
    }

    public LivingEntity player;
    public BlockPos pos;
    public Hand hand;
    public BlockEntity blockEntity;
    public Entity entity;
    public World world;

    public void getHitBlockResult() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            this.player = player;
            this.pos = hitResult.getBlockPos();
            this.hand = hand;
            this.world = world;
            this.blockEntity = world.getBlockEntity(pos);
            //清除Entity的缓存
            this.entity = null;
            return ActionResult.PASS;
        });

    }
    public void getHitEntityResult() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            this.player = player;
            //会抛出空指针异常
            //this.pos = hitResult.getEntity().getBlockPos()
            this.pos = entity.getBlockPos();
            this.hand = hand;
            this.world = world;
            //清除blockEntity的缓存(不知道有没有必要，但保险一点)
            this.blockEntity = null;
            this.entity = entity;
            return ActionResult.PASS;
        });
    }

}