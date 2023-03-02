package me.contaria.dragndrop;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.slf4j.Logger;

public class DragAndDrop implements ClientModInitializer {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ModMetadata DRAG_N_DROP = FabricLoader.getInstance().getModContainer("dragndrop").orElseThrow().getMetadata();
    private static final String LOG_PREFIX = "[" + DRAG_N_DROP.getName() + "] ";

    @Override
    public void onInitializeClient() {
        log("Initializing " + DRAG_N_DROP.getName() + " v" + DRAG_N_DROP.getVersion().getFriendlyString());
    }

    public static void log(String msg) {
        LOGGER.info(LOG_PREFIX + msg);
    }
}