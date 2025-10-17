package com.xingmot.gtmadvancedhatch.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.UUID;

import com.hepdd.gtmthings.utils.TeamUtil;

public class AHUtil {

    public static Component getTeamName(Level level, UUID playerUUID) {
        Component name = Component.translatable("gtmadvancedhatch.machine.unknow_player");
        if (TeamUtil.hasOwner(level, playerUUID))
            name = TeamUtil.GetName(level, playerUUID);
        return name;
    }
}
