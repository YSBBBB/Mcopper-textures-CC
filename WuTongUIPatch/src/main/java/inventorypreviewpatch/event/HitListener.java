package inventorypreviewpatch.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HitListener {

    private static final HitListener INSTANCE = new HitListener();
    public static HitListener getInstance()
    {
        return INSTANCE;
    }

    public BlockPos pos ;
    public Hand hand;
    public BlockEntity blockEntity;
    public World world;

    public void getHitResult() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

            this.pos = hitResult.getBlockPos();
            this.hand = hand;
            this.world = world ;
            this.blockEntity = world.getBlockEntity(pos);
            return ActionResult.PASS;
        });
    }
}