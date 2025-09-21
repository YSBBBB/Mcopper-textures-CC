package inventorypreviewpatch.compat.rei;

import fi.dy.masa.malilib.util.GuiUtils;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;

import java.util.ArrayList;
import java.util.List;

import static inventorypreviewpatch.render.CheatSheetOverlay.isTriggered;

public class ReiRecipePageImpl implements REIClientPlugin {
    private static void registerExclusionZonesFrame(Class<? extends Screen> screenClass, ExclusionZones zones, int x, int y, int width, int height) {
        zones.register(screenClass, provider -> {
            List<Rectangle> areas = new ArrayList<>();
            if (isTriggered(provider)) {
                areas.add(new Rectangle(GuiUtils.getScaledWindowWidth() / 2 + x, GuiUtils.getScaledWindowHeight() / 2 + y, width, height));
            }
            return areas;
        });
    }
    //为小抄适配REI
    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        registerExclusionZonesFrame(BrewingStandScreen.class, zones, -205, -80, 160, 160);
        registerExclusionZonesFrame(AbstractFurnaceScreen.class, zones, -170, -80, 140, 160);
        registerExclusionZonesFrame(HopperScreen.class, zones, 50, -70, 160, 140);
        registerExclusionZonesFrame(SmithingScreen.class, zones, 50, -80, 185, 160);
        registerExclusionZonesFrame(AnvilScreen.class, zones, 50, -80, 110, 160);
    }
}
