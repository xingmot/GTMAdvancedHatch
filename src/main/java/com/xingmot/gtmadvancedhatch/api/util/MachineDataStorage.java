package com.xingmot.gtmadvancedhatch.api.util;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.WeakHashMap;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;

// 中间存储类
public class MachineDataStorage {

    // 包级访问的方法，允许mixin类访问
    @Getter
    @Setter
    private static WeakHashMap<Pair<UUID, MetaMachine>, BigDecimal> convertedData = new WeakHashMap<>();

    // 或者直接操作Map的方法
    public static void put(Pair<UUID, MetaMachine> key, BigDecimal value) {
        convertedData.put(key, value);
    }

    public static BigDecimal get(Pair<UUID, MetaMachine> key) {
        return convertedData.get(key);
    }
}
