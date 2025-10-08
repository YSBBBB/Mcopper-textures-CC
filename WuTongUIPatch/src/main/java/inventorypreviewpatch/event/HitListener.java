package inventorypreviewpatch.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class HitListener {
    public record Context(@Nullable BlockPos pos, @Nullable BlockState blockState, @Nullable BlockEntity be, @Nullable Block block, @Nullable Entity entity, @Nullable Inventory inv, Hand hand, World world) {}

    private static final HitListener INSTANCE = new HitListener();
    private Context context = new Context(null, null, null, null, null, null,null, null);
    public static HitListener getInstance() {
        return INSTANCE;
    }

    public void upDateHitResult() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            BlockEntity be = world.getBlockEntity(pos);
            context = new Context(pos, state, be, state == null? null : state.getBlock(), null, be instanceof Inventory inv? inv : null, hand, world);
            return ActionResult.PASS;
        });
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            context = new Context(entity.getBlockPos(), null, null, null, entity, entity instanceof Inventory inv? inv : null, hand, world);
            return ActionResult.PASS;
        });
    }

    public Context getHitResult() {
        return context;
    }
}