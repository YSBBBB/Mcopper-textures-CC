package inventorypreviewpatch.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import inventorypreviewpatch.Reference;
import inventorypreviewpatch.configs.Configs;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GuiConfigs extends GuiConfigsBase {
    private static ConfigGuiTab tab = ConfigGuiTab.GENERIC;

    public GuiConfigs() {
        super(10, 50, Reference.MOD_ID, null, "inventorypreviewpatch.gui.title.configs", String.format("%s", Reference.MOD_VERSION));
    }

    @Override
    public void initGui() {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;

        for (ConfigGuiTab tab : ConfigGuiTab.values()) {
            x += this.createButton(x, y, -1, tab);
        }
    }

    private int createButton(int x, int y, int width, ConfigGuiTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(GuiConfigs.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth() + 2;
    }

    @Override
    protected int getConfigWidth() {
        ConfigGuiTab tab = GuiConfigs.tab;

        if (tab == ConfigGuiTab.GENERIC) {
            return 120;
        } else if (tab == ConfigGuiTab.FIXES) {
            return 60;
        }
        return 260;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = GuiConfigs.tab;

        if (tab == ConfigGuiTab.GENERIC) {
            configs = Configs.Generic.OPTIONS;
        } else if (tab == ConfigGuiTab.FIXES) {
            configs = Configs.Fixes.OPTIONS;
        } else {
            return Collections.emptyList();
        }

        return ConfigOptionWrapper.createFor(configs);
    }

    public enum ConfigGuiTab {
        GENERIC("inventorypreviewpatch.gui.button.config_gui.generic"),
        FIXES("inventorypreviewpatch.gui.button.config_gui.fixes");

        private final String translationKey;

        ConfigGuiTab(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getDisplayName() {
            return StringUtils.translate(this.translationKey);
        }
    }

    private record ButtonListener(ConfigGuiTab tab, GuiConfigs parent) implements IButtonActionListener {
        @Override
            public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
                GuiConfigs.tab = this.tab;
                this.parent.reCreateListWidget();
                Objects.requireNonNull(this.parent.getListWidget()).resetScrollbarPosition();
                this.parent.initGui();
            }
        }
}
