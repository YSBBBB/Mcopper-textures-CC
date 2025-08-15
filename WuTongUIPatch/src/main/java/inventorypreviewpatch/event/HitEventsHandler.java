package inventorypreviewpatch.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HitEventsHandler {

    private static final HitEventsHandler INSTANCE = new HitEventsHandler();
    public static HitEventsHandler getInstance()
    {
        return INSTANCE;
    }

    public BlockPos pos = null;
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