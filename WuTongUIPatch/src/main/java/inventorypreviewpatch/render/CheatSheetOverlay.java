package inventorypreviewpatch.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.util.GuiUtils;
import inventorypreviewpatch.mixin.Accessors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static inventorypreviewpatch.configs.Configs.Generic.USE_CHEAT_SHEET;
import static inventorypreviewpatch.event.ResourcesLoadedListener.isLoadedWuTongUI;

//为小抄单开一个类
public class CheatSheetOverlay {
    private static final Identifier CheatSheetTexture_Furnace = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/furnace.png");
    private static final Identifier CheatSheetTexture_Anvil = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/anvil.png");
    private static final Identifier CheatSheetTexture_Smithing = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/smithing.png");
    private static final Identifier CheatSheetTexture_Hopper = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/hopper.png");
    private static final Identifier CheatSheetTexture_BrewingStandI = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/brewing_stand.png");
    private static final Identifier CheatSheetTexture_BrewingStandII = Identifier.ofVanilla("textures/font/b100.png");
    private static final Identifier Texture_Button_On = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/button_on.png");
    private static final Identifier Texture_Button_Off = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/button_off.png");
    private static final Map<String, int[]> cheat_position = new HashMap<>();
    public static boolean isUseFurnaceCheatSheet = false;
    public static boolean isUseAnvilCheatSheet = false;
    public static boolean isUseBrewingStandCheatSheet = false;
    public static boolean isUseSmithingSCheatSheet = false;
    public static boolean isUseHopperCheatSheet = false;
    private static int width = 16;
    private static int height = 16;
    private static boolean book_isOpen = false;

    private static ButtonWidget buttonWidget;
    private static int[] position;

    private static void calculatePosition(Screen screen) {
        if (isLoadedWuTongUI()) {
            position = switch (screen) {
                case AbstractFurnaceScreen<?> ignore -> book_isOpen ? new int[]{70, -80} : new int[]{-8, -80};
                case AnvilScreen ignore -> new int[]{-8, -80};
                case BrewingStandScreen ignore -> new int[]{-9, -85};
                case SmithingScreen ignore -> new int[]{-8, -80};
                default -> new int[]{-8, -70};
            };
        } else {
            position = switch (screen) {
                case AbstractFurnaceScreen<?> ignore ->
                        book_isOpen ? cheat_position.get("furnace_on") : cheat_position.get("furnace_off");
                case HopperScreen ignore -> cheat_position.get("hopper");
                case BrewingStandScreen ignore -> cheat_position.get("brewingStand");
                case AnvilScreen ignore -> cheat_position.get("anvil");
                case SmithingScreen ignore -> cheat_position.get("smithing");
                default -> new int[]{-8, -18};
            };
        }
    }

    public static void addCheatSheetButton(Screen screen) {
        if (!isCheatSheetScreen(screen) && USE_CHEAT_SHEET.getStringValue().equals("customization")) {
            boolean isUse = USE_CHEAT_SHEET.getStringValue().equals("all");
            isUseFurnaceCheatSheet = isUse;
            isUseAnvilCheatSheet = isUse;
            isUseBrewingStandCheatSheet = USE_CHEAT_SHEET.getStringValue().equals("brewingStand") || isUse;
            isUseSmithingSCheatSheet = isUse;
            isUseHopperCheatSheet = isUse;
            return;
        }
        calculatePosition(screen);
        buttonWidget = ButtonWidget.builder(Text.literal("cheat_button"), button1 -> {
            switch (screen) {
                case AbstractFurnaceScreen<?> furnace -> {
                    isUseFurnaceCheatSheet = !isUseFurnaceCheatSheet;
                    if (!book_isOpen) return;
                    final int recipeBookButton_X = (furnace.width - 176) / 2 + 97;
                    final int recipeBookButton_Y = furnace.height / 2 - 49;
                    Optional<Element> optional = furnace.hoveredElement(recipeBookButton_X, recipeBookButton_Y);
                    if (optional.isPresent() && optional.get() instanceof ButtonWidget recipeButton) {
                        recipeButton.onPress();
                    }
                }
                case BrewingStandScreen ignored -> isUseBrewingStandCheatSheet = !isUseBrewingStandCheatSheet;
                case AnvilScreen ignored -> isUseAnvilCheatSheet = !isUseAnvilCheatSheet;
                case SmithingScreen ignored -> isUseSmithingSCheatSheet = !isUseSmithingSCheatSheet;
                case HopperScreen ignored -> isUseHopperCheatSheet = !isUseHopperCheatSheet;
                default -> {
                }
            }
        }).size(width, height).build();
        ((Accessors.ScreenAccessor) screen).inventory_preview_fix_addSelectableChild(buttonWidget);
    }

    public static void renderButton(DrawContext context, Screen screen) {
        if (!isCheatSheetScreen(screen) || !USE_CHEAT_SHEET.getStringValue().equals("customization")) return;
        int x = GuiUtils.getScaledWindowWidth() / 2 + position[0];
        int y = GuiUtils.getScaledWindowHeight() / 2 + position[1];
        if (screen instanceof AbstractFurnaceScreen<?> furnace) {
            boolean book_isOpen = ((Accessors.RecipeBookScreenAccessor) furnace).inventory_preview_fix_getRecipeBook().isOpen();
            if (CheatSheetOverlay.book_isOpen != book_isOpen) {
                CheatSheetOverlay.book_isOpen = book_isOpen;
                calculatePosition(screen);
            }
            if (CheatSheetOverlay.book_isOpen) {
                isUseFurnaceCheatSheet = false;
            }
        }
        if (!screen.children().contains(buttonWidget)) {
            buttonWidget.setPosition(x, y);
            ((Accessors.ScreenAccessor) screen).inventory_preview_fix_addSelectableChild(buttonWidget);
        }
        if (buttonWidget.getX() != x || buttonWidget.getY() != y) {
            buttonWidget.setPosition(x, y);
        }
        context.drawTexture(RenderLayer::getGuiTextured, isTriggered(screen) ? Texture_Button_On : Texture_Button_Off, x, y, 0.0F, 0.0f, width, height, 16, 16);
    }

    public static void renderCheatSheet(DrawContext context, Screen screen, int xCenter, int yCenter) {
        renderButton(context, screen);
        if (!isTriggered(screen)) return;
        switch (screen) {
            case AbstractFurnaceScreen<?> ignore -> {
                int x = book_isOpen ? 158 : 178;
                context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture_Furnace, xCenter - x, yCenter - 83, 9.07766f, 0.6496f, 90, 166, 99, 166);
            }
            case BrewingStandScreen ignore -> {
                if (isLoadedWuTongUI())
                    context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture_BrewingStandII, xCenter - 205, yCenter - 78, 7.7808f, 0.6484f, 120, 162, 160, 166);
                else
                    context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture_BrewingStandI, xCenter - 185, yCenter - 82, 0f, 0.6484f, 120, 170, 152, 170);
            }
            case AnvilScreen ignored ->
                    context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture_Anvil, xCenter + 87, yCenter - 82, 0, 0.6496f, 73, 167, 127, 168);
            case SmithingScreen ignored ->
                    context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture_Smithing, xCenter + 88, yCenter - 82, 0, 1.2188f, 144, 167, 144, 168);
            default ->
                    context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture_Hopper, xCenter + 87, yCenter - 72, 1, 0, 138, 145, 138, 145);
        }
    }

    public static void loadChestButtonParameters(JsonObject object) {
        cheat_position.clear();
        cheat_position.put("furnace_on", new int[]{70, -80});
        cheat_position.put("furnace_off", new int[]{-8, -80});
        cheat_position.put("brewingStand", new int[]{-9, -83});
        cheat_position.put("hopper", new int[]{-8, -31});
        cheat_position.put("anvil", new int[]{-8, -18});
        cheat_position.put("smithing", new int[]{-8, -18});
        if (object == null || object.has("cheat_button")) return;
        JsonArray cheat_button_position = object.get("cheat_button").getAsJsonArray();
        for (int i = 0; i < cheat_button_position.size(); i++) {
            JsonObject subObject = cheat_button_position.get(i).getAsJsonObject();
            if (!subObject.has("id")) continue;
            if (subObject.get("id").getAsString().equals("size")) {
                width = subObject.has("width") ? subObject.get("width").getAsInt() : 16;
                height = subObject.has("height") ? subObject.get("height").getAsInt() : 16;
                continue;
            }
            if (subObject.has("x") && subObject.has("y")) {
                int x = subObject.get("x").getAsInt();
                int y = subObject.get("y").getAsInt();
                cheat_position.put(subObject.get("id").getAsString(), new int[]{x, y});
            }
        }
    }

    public static boolean isCheatSheetScreen(Screen screen) {
        return switch (screen) {
            case AbstractFurnaceScreen<?> ignore -> true;
            case AnvilScreen ignore -> true;
            case BrewingStandScreen ignore -> true;
            case SmithingScreen ignore -> true;
            case HopperScreen ignore -> true;
            default -> false;
        };
    }

    public static boolean isTriggered(Screen screen) {
        return switch (screen) {
            case AbstractFurnaceScreen<?> ignore when isUseFurnaceCheatSheet -> true;
            case AnvilScreen ignore when isUseAnvilCheatSheet -> true;
            case BrewingStandScreen ignore when isUseBrewingStandCheatSheet -> true;
            case SmithingScreen ignore when isUseSmithingSCheatSheet -> true;
            case HopperScreen ignore when isUseHopperCheatSheet -> true;
            default -> false;
        };
    }
}