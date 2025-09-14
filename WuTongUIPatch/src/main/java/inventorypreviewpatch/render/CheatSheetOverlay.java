package inventorypreviewpatch.render;

import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static inventorypreviewpatch.configs.Configs.Generic.USE_CHEAT_SHEET;
import static inventorypreviewpatch.event.ResourcesLoadedListener.isLoadedWuTongUI;

//为小抄单开一个类
public class CheatSheetOverlay {
    private static final Identifier CheatSheetTexture_Furnace = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/furnace.png");
    private static final Identifier CheatSheetTexture_Anvil = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/anvil.png");
    private static final Identifier CheatSheetTexture_Smithing = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/smithing.png");
    private static final Identifier CheatSheetTexture_Hopper = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/hopper.png");
    private static final Identifier CheatSheetTexture_BrewingStand = Identifier.ofVanilla("textures/font/b100.png");
    private static final int width = 16;
    private static final int height = 16;
    public static boolean isUseFurnaceCheatSheet = false;
    public static boolean isUseAnvilCheatSheet = false;
    public static boolean isUseBrewingStandCheatSheet = false;
    public static boolean isUseSmithingSCheatSheet = false;
    public static boolean isUseHopperCheatSheet = false;
    private static int[] position = new int[0];

    private static void calculatePosition(Screen screen) {
        if (isLoadedWuTongUI) {
            position = switch (screen) {
                case AbstractFurnaceScreen<?> furnace ->
                        furnace.recipeBook.isOpen() ? new int[]{70, -80} : new int[]{-8, -80};
                case AnvilScreen ignore -> new int[]{-8, -80};
                case BrewingStandScreen ignore -> new int[]{-9, -85};
                case SmithingScreen ignore -> new int[]{-8, -80};
                default -> new int[]{-8, -70};
            };
        } else {
            position = switch (screen) {
                case AbstractFurnaceScreen<?> furnace ->
                        furnace.recipeBook.isOpen() ? new int[]{70, -18} : new int[]{-8, -18};
                case HopperScreen ignore -> new int[]{-8, -31};
                default -> new int[]{-8, -18};
            };
        }
    }

    public static void addCheatSheetButton(Screen screen) {
        if (!isLoadedWuTongUI && screen instanceof BrewingStandScreen) return;
        if (USE_CHEAT_SHEET.getStringValue().equals("all") || USE_CHEAT_SHEET.getStringValue().equals("no") || USE_CHEAT_SHEET.getStringValue().equals("brewingStand")) {
            boolean isUse = USE_CHEAT_SHEET.getStringValue().equals("all");
            isUseFurnaceCheatSheet = isUse;
            isUseAnvilCheatSheet = isUse;
            isUseBrewingStandCheatSheet = USE_CHEAT_SHEET.getStringValue().equals("brewingStand") || isUse;
            isUseSmithingSCheatSheet = isUse;
            isUseHopperCheatSheet = isUse;
            return;
        }
        if (isCheatSheetScreen(screen) && USE_CHEAT_SHEET.getStringValue().equals("customization")) {
            int x = GuiUtils.getScaledWindowWidth() / 2 + position[0];
            int y = GuiUtils.getScaledWindowHeight() / 2 + position[1];
            ButtonWidget button = ButtonWidget.builder(Text.literal(""), button1 -> {
                switch (screen) {
                    case AbstractFurnaceScreen<?> furnace -> {
                        if (furnace.recipeBook.isOpen()) {
                            final int recipeBookButton_X = (furnace.width - 176) / 2 + 97;
                            final int recipeBookButton_Y = furnace.height / 2 - 49;
                            //在配方书开启时会关闭配方书，开启小抄
                            for (Element element : furnace.children) {
                                if (element instanceof ButtonWidget buttonWidget
                                        && buttonWidget.getX() == recipeBookButton_X
                                        && buttonWidget.getY() == recipeBookButton_Y
                                ) {
                                    buttonWidget.onPress();
                                    break;
                                }
                            }
                        }
                        isUseFurnaceCheatSheet = !isUseFurnaceCheatSheet;
                    }
                    case BrewingStandScreen ignored -> isUseBrewingStandCheatSheet = !isUseBrewingStandCheatSheet;
                    case AnvilScreen ignored -> isUseAnvilCheatSheet = !isUseAnvilCheatSheet;
                    case SmithingScreen ignored -> isUseSmithingSCheatSheet = !isUseSmithingSCheatSheet;
                    case HopperScreen ignored -> isUseHopperCheatSheet = !isUseHopperCheatSheet;
                    default -> {
                    }
                }
            }).dimensions(x, y, width, height).build();

            if (screen.children().equals(button)) {
                if (button.getX() != x || button.getY() != y) {
                    button.setPosition(x, y);
                }
            }
            screen.children.add(button);
        }
    }

    public static void renderButton(DrawContext context, Screen screen) {
        if (!isLoadedWuTongUI && screen instanceof BrewingStandScreen) return;
        if (isCheatSheetScreen(screen) && USE_CHEAT_SHEET.getStringValue().equals("customization")) {
            int x = GuiUtils.getScaledWindowWidth() / 2 + position[0];
            int y = GuiUtils.getScaledWindowHeight() / 2 + position[1];
            String buttonState = isTriggered(screen) ? "on" : "off";
            if (screen instanceof AbstractFurnaceScreen<?> furnace) {
                if (furnace.recipeBook.isOpen()) {
                    isUseFurnaceCheatSheet = false;
                    buttonState = "off";
                }
            }
            Identifier Texture_Button = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/button_" + buttonState + ".png");
            context.drawTexture(RenderLayer::getGuiTextured, Texture_Button, x, y, 0.0F, 0.0f, width, height, 16, 16);
        }
    }

    public static void renderCheatSheet(DrawContext context, Screen screen, int xCenter, int yCenter) {
        calculatePosition(screen);
        addCheatSheetButton(screen);
        renderButton(context, screen);
        if (isTriggered(screen)) {
            switch (screen) {
                case AbstractFurnaceScreen<?> furnace -> {
                    int x = furnace.recipeBook.isOpen() ? 158 : 178;
                    context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture_Furnace, xCenter - x, yCenter - 83, 9.07766f, 0.6496f, 90, 166, 99, 166);
                }
                case BrewingStandScreen ignore when isLoadedWuTongUI ->
                        context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture_BrewingStand, xCenter - 205, yCenter - 78, 7.7808f, 0.6484f, 120, 162, 160, 166);
                case AnvilScreen ignored ->
                        context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture_Anvil, xCenter + 87, yCenter - 82, 0, 0.6496f, 73, 167, 127, 168);
                case SmithingScreen ignored ->
                        context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture_Smithing, xCenter + 88, yCenter - 82, 0, 1.2188f, 144, 167, 144, 168);
                default ->
                        context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture_Hopper, xCenter + 87, yCenter - 72, 1, 0, 138, 145, 138, 145);
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