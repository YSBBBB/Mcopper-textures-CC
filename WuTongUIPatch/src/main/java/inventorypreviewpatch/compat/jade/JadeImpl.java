package inventorypreviewpatch.compat.jade;

import inventorypreviewpatch.ModUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import snownee.jade.api.Accessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.ui.IBoxElement;
import snownee.jade.api.ui.TooltipRect;

import static inventorypreviewpatch.configs.Configs.Fixes.JADE_TOOLTIP_FIXES;

public class JadeImpl implements IWailaPlugin{
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addBeforeRenderCallback((IBoxElement element, TooltipRect rect, DrawContext context, Accessor<?> accessor) ->
                JADE_TOOLTIP_FIXES.getBooleanValue() && ModUtils.isOnPreview(mc));
    }
}
