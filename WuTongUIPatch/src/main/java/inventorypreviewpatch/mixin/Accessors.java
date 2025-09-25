package inventorypreviewpatch.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
public class Accessors {
    @Mixin(Screen.class)
    public interface ScreenAccessor {
        @Accessor("title")
        void inventory_preview_fix_setTitle(Text newTitle);

        @Invoker("addSelectableChild")
        <T extends Element & Selectable> T inventory_preview_fix_addSelectableChild(T child);
    }

    @Mixin(HandledScreen.class)
    public interface HandledScreenAccessor {
        @Accessor("playerInventoryTitle")
        void inventory_preview_fix_setPlayerInventoryTitle(Text newTitle);
    }

    @Mixin(ScreenHandler.class)
    public interface ScreenHandlerAccessor {
        @Accessor("type")
        ScreenHandlerType<?> inventory_preview_fix_getType();
    }

    @Mixin(AbstractFurnaceBlockEntity.class)
    public interface AbstractFurnaceBlockEntityAccessor {
        @Accessor("propertyDelegate")
        PropertyDelegate inventory_preview_fix_getPropertyDelegate();
    }

    @Mixin(BrewingStandBlockEntity.class)
    public interface BrewingStandBlockEntityAccessor {
        @Accessor("propertyDelegate")
        PropertyDelegate inventory_preview_fix_getPropertyDelegate();
    }

    @Mixin(RecipeBookScreen.class)
    public interface RecipeBookScreenAccessor {
        @Accessor("recipeBook")
        RecipeBookWidget<?> inventory_preview_fix_getRecipeBook();
    }
}
