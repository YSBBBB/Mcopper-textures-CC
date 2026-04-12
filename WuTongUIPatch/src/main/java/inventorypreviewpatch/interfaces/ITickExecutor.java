package inventorypreviewpatch.interfaces;

import inventorypreviewpatch.event.ModRenderEventHandler;
import net.minecraft.client.MinecraftClient;

public interface ITickExecutor {

    /**
     * 在每tick开始时调用
     * 注册方法为{@link ModRenderEventHandler#registerTickExecutor(ITickExecutor)}
     * @param client ()
     * @param partialTicks ()
     */
    default void executeOnTickStarted(MinecraftClient client, float partialTicks) {}

    /**
     * 在每tick结束时调用
     * 注册方法为{@link ModRenderEventHandler#registerTickExecutor(ITickExecutor)}
     * @param client ()
     * @param partialTicks ()
     */
    default void executeOnTickEnded(MinecraftClient client, float partialTicks) {}

}
