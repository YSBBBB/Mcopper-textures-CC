package inventorypreviewpatch;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static inventorypreviewpatch.Reference.MOD_ID;

public class WuTongUIPatch implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        InitHandler.getInstance().registerModHandlers();

        LOGGER.info("Hello Fabric world!");
    }

}