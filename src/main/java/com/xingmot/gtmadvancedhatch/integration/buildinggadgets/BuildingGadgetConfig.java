package com.xingmot.gtmadvancedhatch.integration.buildinggadgets;

import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;
import com.xingmot.gtmadvancedhatch.config.AHConfig;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

@Config(id = GTMAdvancedHatch.MODID)
public class BuildingGadgetConfig {
    public static BuildingGadgetConfig INSTANCE;
    public static final Object lock = new Object();

    public static void init() {
        synchronized (lock) {
            if (INSTANCE == null) {
                INSTANCE = Configuration.registerConfig(BuildingGadgetConfig.class, ConfigFormats.yaml()).getConfigInstance();
            }
        }
    }

    @Configurable
    @Configurable.Range(min=100,max=Integer.MAX_VALUE)
    @Configurable.Comment({"默认是一般复制工具的100倍容量:100M"})
    public Integer buildingGadgetMaxPower = 100000000;
}
