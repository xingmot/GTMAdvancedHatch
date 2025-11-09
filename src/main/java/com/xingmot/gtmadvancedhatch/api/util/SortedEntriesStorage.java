package com.xingmot.gtmadvancedhatch.api.util;

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
                    .sorted((entry1, entry2) -> {
                        BigDecimal value1 = entry1.getValue();
                        BigDecimal value2 = entry2.getValue();

                        // 大于0按自然顺序(升序)，小于0按倒序(降序)
                        if (value1.compareTo(BigDecimal.ZERO) > 0 && value2.compareTo(BigDecimal.ZERO) > 0) {
                            return value1.compareTo(value2);  // 升序
                        } else {
                            return value2.compareTo(value1);  // 降序
                        }
                    })
                    .toList();
            machineData.clear();
        }
        return sortedEntries;
    }
}
