package com.xingmot.gtmadvancedhatch.util;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class ModUtil {

    // from glodium mod(mc1.21) by Yang Xizhi
    public static boolean checkMod(String modid) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods()
                    .stream().map(ModInfo::getModId)
                    .anyMatch(modid::equals);
        } else {
            return ModList.get().isLoaded(modid);
        }
    }
}
