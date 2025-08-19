package inventorypreviewpatch;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WuTongUIPatch implements ModInitializer{
    public static final String MOD_ID = "inventorypreviewpatch";
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        InitHandler.getInstance().registerModHandlers();

        LOGGER.info("Hello Fabric world!");
    }

}