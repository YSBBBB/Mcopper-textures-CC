package inventorypreviewpatch.render;

import inventorypreviewpatch.interfaces.ITickExecutor;
import net.minecraft.client.MinecraftClient;

public class TickExecutor implements ITickExecutor {
    @Override
    public void executeOnTickStarted(MinecraftClient client, float partialTicks) {
        //负责写入
        CreeperForewarnOverlay.updateState(client, partialTicks);
    }
}
