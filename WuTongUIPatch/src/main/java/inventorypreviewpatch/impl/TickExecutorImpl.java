package inventorypreviewpatch.impl;

import inventorypreviewpatch.interfaces.ITickExecutor;
import inventorypreviewpatch.render.CreeperForewarnOverlay;
import net.minecraft.client.MinecraftClient;

public class TickExecutorImpl implements ITickExecutor {
    @Override
    public void executeOnTickStarted(MinecraftClient client, float partialTicks) {
        //负责写入
        CreeperForewarnOverlay.updateState(client, partialTicks);
    }
}
