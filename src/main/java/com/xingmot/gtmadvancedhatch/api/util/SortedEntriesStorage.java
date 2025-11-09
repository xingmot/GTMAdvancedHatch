package com.xingmot.gtmadvancedhatch.api.util;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;
import lombok.Setter;

// 中间存储类
public class SortedEntriesStorage {

    // 包级访问的方法，允许mixin类访问
    @Setter
    private static List<Map.Entry<Pair<UUID, MetaMachine>, BigDecimal>> sortedEntries = null;

    public static List<Map.Entry<Pair<UUID, MetaMachine>, BigDecimal>> getSortedEntries(Long offsetTimer) {
        if (sortedEntries == null || offsetTimer % 20 == 0) {
            WeakHashMap<Pair<UUID, MetaMachine>, BigDecimal> machineData = MachineDataStorage.getConvertedData();
            sortedEntries = machineData.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey() != null) // 过滤 null 键
                    .sorted((e1, e2) -> {
                        BigDecimal v1 = e1.getValue();
                        BigDecimal v2 = e2.getValue();

                        // 如果都是正数，按降序排列
                        if (v1.compareTo(BigDecimal.ZERO) >= 0 && v2.compareTo(BigDecimal.ZERO) >= 0) {
                            return v2.compareTo(v1); // 降序
                        }
                        // 如果都是负数，按升序排列（绝对值降序）
                        else if (v1.compareTo(BigDecimal.ZERO) < 0 && v2.compareTo(BigDecimal.ZERO) < 0) {
                            return v1.compareTo(v2); // 升序
                        }
                        // 一正一负，正数在前
                        else {
                            return v1.compareTo(BigDecimal.ZERO) >= 0 ? -1 : 1;
                        }
                    })
                    .collect(Collectors.toList());

            machineData.clear();
        }
        return sortedEntries;
    }
}
