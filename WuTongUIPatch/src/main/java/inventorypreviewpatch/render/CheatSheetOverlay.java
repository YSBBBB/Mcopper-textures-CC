package inventorypreviewpatch.render;

import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static inventorypreviewpatch.configs.Configs.Generic.Use_Cheat_Sheet;
import static inventorypreviewpatch.event.ResourcesLoadedListener.isLoadedWuTongUI;

//为小抄单开一个类
public class CheatSheetOverlay {

    public static boolean isUseFurnaceCheatSheet = false;
    public static boolean isUseAnvilCheatSheet = false;
    public static boolean isUseBrewingStandCheatSheet = false;
    public static boolean isUseSmithingSCheatSheet = false;
    public static boolean isUseHopperCheatSheet = false;

    public int x;
    public int y;
    public static final int width = 16;
    public static final int height = 16;
    Screen screen;

    protected CheatSheetOverlay(Screen screen) {
        this.screen = screen;
        if (!isLoadedWuTongUI) {
            x = -8;
            y = -18;
            if (screen instanceof AbstractFurnaceScreen<?> furnace) {
                if (furnace.recipeBook.isOpen()) {
                    x = 70;
                }
            } else if (screen instanceof HopperScreen) {
                y = -30;
            }
            return;
        }
        switch (screen) {
            case AbstractFurnaceScreen<?> furnace -> {
                this.x = furnace.recipeBook.isOpen() ? 70 : -8;
                this.y = -80;
            }
            case AnvilScreen ignore -> {
                this.x = -8;
                this.y = -80;

            }
            case BrewingStandScreen ignore -> {
                this.x = -9;
                this.y = -85;
            }
            case SmithingScreen ignore -> {
                this.x = -8;
                this.y = -80;
            }
            default -> {
                this.x = -8;
                this.y = -70;
            }
        }
    }

    public static CheatSheetOverlay getInstance(Screen screen) {
        return new CheatSheetOverlay(screen);
    }

    public void addCheatSheetButton() {
        if (!isLoadedWuTongUI && screen instanceof BrewingStandScreen) return;
        if (Use_Cheat_Sheet.getStringValue().equals("all") || Use_Cheat_Sheet.getStringValue().equals("no") || Use_Cheat_Sheet.getStringValue().equals("brewingStand")) {
            boolean isUse = Use_Cheat_Sheet.getStringValue().equals("all");
            isUseFurnaceCheatSheet = isUse;
            isUseAnvilCheatSheet = isUse;
            isUseBrewingStandCheatSheet =  Use_Cheat_Sheet.getStringValue().equals("brewingStand") || isUse;
            isUseSmithingSCheatSheet = isUse;
            isUseHopperCheatSheet = isUse;
            return;
        }
        if (isCheatSheetScreen(screen) && Use_Cheat_Sheet.getStringValue().equals("customization")) {
            ButtonWidget button = ButtonWidget.builder(Text.literal(""), button1 -> {
                        switch (screen) {
                            case AbstractFurnaceScreen<?> furnace -> {
                                //在配方书开启时会关闭配方书，开启小抄
                                if (furnace.recipeBook.isOpen()) {
                                    if (furnace.children().getFirst() instanceof ButtonWidget recipeBookButton) {
                                        recipeBookButton.onPress();
                                    }
                                }
                                isUseFurnaceCheatSheet = !isUseFurnaceCheatSheet;
                            }
                            case AnvilScreen ignored -> isUseAnvilCheatSheet = !isUseAnvilCheatSheet;
                            case BrewingStandScreen ignored -> isUseBrewingStandCheatSheet = !isUseBrewingStandCheatSheet;
                            case SmithingScreen ignored -> isUseSmithingSCheatSheet = !isUseSmithingSCheatSheet;
                            case HopperScreen ignored -> isUseHopperCheatSheet = !isUseHopperCheatSheet;
                            default -> {}
                        }
                    })
                    .dimensions(GuiUtils.getScaledWindowWidth() / 2 + x, GuiUtils.getScaledWindowHeight() / 2 + y, width, height)
                    .build();

            screen.children.remove(button);
            screen.children.add(button);
        }
    }

    public void renderButton(DrawContext context, int xCenter, int yCenter) {
        if (!isLoadedWuTongUI && screen instanceof BrewingStandScreen) return;
        if (isCheatSheetScreen(screen) && Use_Cheat_Sheet.getStringValue().equals("customization")) {
            String buttonState = isTriggered(screen)? "on" : "off";
            if (screen instanceof AbstractFurnaceScreen<?> furnace) {
                if (furnace.recipeBook.isOpen()) {
                    isUseFurnaceCheatSheet = false;
                    buttonState = "off";
                }
            }
            Identifier Texture_button = Identifier.of("inventorypreviewpatch","textures/cheatsheet/button_"+ buttonState +".png");
            context.drawTexture(RenderLayer::getGuiTextured, Texture_button, xCenter + x , yCenter + y, 0.0F, 0.0f, width, height, 16, 16);
        }
    }

    public void renderCheatSheet(DrawContext context, int xCenter, int yCenter) {
        if (isTriggered(screen)) {
            switch (screen) {
                case AbstractFurnaceScreen<?> furnace -> {
                    int x;
                    if(furnace.recipeBook.isOpen()) {
                        x = 158;
                    } else {
                        x = 178;
                    }
                    final Identifier CheatSheetTexture = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/furnace.png");
                    context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture, xCenter - x, yCenter - 83, 9.07766f, 0.6496f, 90, 166, 99, 166);
                }
                case AnvilScreen ignored -> {
                    final Identifier CheatSheetTexture = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/anvil.png");
                    context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture, xCenter + 87, yCenter - 82, 0, 0.6496f, 73, 167, 127, 168);
                }
                case SmithingScreen ignored -> {
                    final Identifier CheatSheetTexture = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/smithing.png");

                    context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture, xCenter + 88, yCenter - 82, 0, 1.2188f, 144, 167, 144, 168);
                }
                case HopperScreen ignored -> {
                    final Identifier CheatSheetTexture = Identifier.of("inventorypreviewpatch", "textures/cheatsheet/hopper.png");

                     context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture,  xCenter + 87, yCenter - 72, 1, 0, 138, 145, 138, 145);
                }
                //酿造台
                default -> {
                    if (isLoadedWuTongUI) {
                        final Identifier CheatSheetTexture = Identifier.ofVanilla("textures/font/b100.png");
                        context.drawTexture(RenderLayer::getGuiTextured, CheatSheetTexture, xCenter - 205, yCenter - 78, 7.7808f, 0.6484f, 120, 162, 160, 166);
                    }
                }
            }
        }
    }

    public static boolean isCheatSheetScreen(Screen screen) {
        return 
                screen instanceof AbstractFurnaceScreen<?> || 
                screen instanceof AnvilScreen || 
                screen instanceof BrewingStandScreen ||
                screen instanceof SmithingScreen || 
                screen instanceof HopperScreen;
    }

    public static boolean isTriggered(Screen screen) {
        return 
                (screen instanceof AbstractFurnaceScreen<?> && isUseFurnaceCheatSheet) ||
                (screen instanceof AnvilScreen && isUseAnvilCheatSheet) ||
                (screen instanceof BrewingStandScreen && isUseBrewingStandCheatSheet) ||
                (screen instanceof SmithingScreen && isUseSmithingSCheatSheet) ||
                (screen instanceof HopperScreen && isUseHopperCheatSheet);
    }

}