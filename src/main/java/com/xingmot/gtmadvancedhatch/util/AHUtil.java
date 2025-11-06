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

    public static long addWithBounds(long a, long b) {
        long result = a + b;

        // 检查正溢出
        if (a > 0 && b > 0 && result < 0) {
            return Long.MAX_VALUE;
        }
        // 检查负溢出
        if (a < 0 && b < 0 && result > 0) {
            return Long.MIN_VALUE;
        }

        return result;
    }

    public static long multiplyWithBounds(long a, long b) {
        if (a == 0 || b == 0) return 0;

        long result = a * b;

        // 检查溢出
        if (a == Long.MIN_VALUE && b == -1) {
            return Long.MAX_VALUE; // 特殊情况
        }
        if (result / a != b) { // 溢出检查
            return ((a > 0 && b > 0) || (a < 0 && b < 0)) ?
                    Long.MAX_VALUE : Long.MIN_VALUE;
        }

        return result;
    }

    public static long divWithBounds(long a, long b) {
        if (b == 0) return a > 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
        long result = a / b;
        // 检查溢出
        if (a == Long.MIN_VALUE && b == -1) {
            return Long.MAX_VALUE; // 特殊情况
        }
        return result;
    }
}
