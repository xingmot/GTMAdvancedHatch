package com.xingmot.gtmadvancedhatch.extra;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

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
                    .sorted(Map.Entry.comparingByValue())
                    .toList();
            machineData.clear();
        }
        return sortedEntries;
    }
}
