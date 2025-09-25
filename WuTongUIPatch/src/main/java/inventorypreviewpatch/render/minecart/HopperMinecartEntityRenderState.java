package inventorypreviewpatch.render.minecart;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;

@Environment(EnvType.CLIENT)
public class HopperMinecartEntityRenderState extends MinecartEntityRenderState {
    public boolean rendering_enabled = false;
    public boolean logic_enabled = false;
}
