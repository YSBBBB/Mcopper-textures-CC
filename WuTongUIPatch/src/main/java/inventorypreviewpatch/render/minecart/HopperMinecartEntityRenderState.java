package inventorypreviewpatch.render.minecart;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.entity.vehicle.HopperMinecartEntity;

@Environment(EnvType.CLIENT)
public class HopperMinecartEntityRenderState extends MinecartEntityRenderState {
    public HopperMinecartEntity shadowOfServer;
    public boolean rendering_enabled = true;
    public boolean logic_enabled = true;
}
