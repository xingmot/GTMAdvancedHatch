package com.xingmot.gtmadvancedhatch.api.adaptivenet;

import net.minecraft.nbt.CompoundTag;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

/**
 * 参考自 ExtendedAE Plus by.GaLicn
 * - 修改0频率也能注册，方便统计数量
 */
public final class NetMasterRegistry {

    private NetMasterRegistry() {}

    // 反向映射，用于处理迁移操作
    private static final Map<NetKey, WeakReference<INetEndpoint>> MASTERS = new HashMap<>();
    private static final Map<NetKey, Long> MASTERS_COUNT = new HashMap<>();

    public static synchronized boolean register(@Nonnull String type, long frequency, @Nullable UUID uuid, INetEndpoint endpoint) {
        if (frequency == 0) return false;
        final NetKey netKey = new NetKey(type, frequency, uuid);

        cleanupIfCleared(netKey);
        var existing = MASTERS.get(netKey);
        var existingVal = existing == null ? null : existing.get();
        if (existingVal != null && !existingVal.isEndpointRemoved()) {
            // 同维度同频率同所有者已经有主端
            return false;
        }
        MASTERS.put(netKey, new WeakReference<>(endpoint));
        MASTERS_COUNT.put(netKey, 0L);
        return true;
    }

    public static synchronized void unregister(@Nonnull String type, long frequency, @Nullable UUID uuid, INetEndpoint endpoint) {
        if (frequency == 0) return;
        final NetKey netKey = new NetKey(type, frequency, uuid);

        var ref = MASTERS.get(netKey);
        if (ref != null) {
            var cur = ref.get();
            if (cur == null || cur == endpoint) {
                MASTERS.remove(netKey);
                MASTERS_COUNT.remove(netKey);
            }
        }
    }

    public static synchronized INetEndpoint get(@Nonnull String type, long frequency, @Nullable UUID uuid) {
        if (frequency == 0) return null;
        final NetKey netKey = new NetKey(type, frequency, uuid);

        cleanupIfCleared(netKey);
        var ref = MASTERS.get(netKey);
        return ref == null ? null : ref.get();
    }

    public static synchronized Long getCount(@Nonnull String type, long frequency, @Nullable UUID uuid) {
        if (frequency == 0) return 0L;
        final NetKey netKey = new NetKey(type, frequency, uuid);

        cleanupIfCleared(netKey);
        return MASTERS_COUNT.get(netKey);
    }

    public static synchronized void addCount(@Nonnull String type, long frequency, @Nullable UUID uuid, int i) {
        if (frequency == 0) return;
        final NetKey netKey = new NetKey(type, frequency, uuid);

        cleanupIfCleared(netKey);
        MASTERS_COUNT.put(netKey, MASTERS_COUNT.get(netKey) + i);
    }

    /** 主要是方便终端和适配仓一起用 */
    public static synchronized CompoundTag getData(@Nonnull String type, long frequency, @Nullable UUID uuid) {
        if (frequency == 0) return null;
        final NetKey netKey = new NetKey(type, frequency, uuid);

        cleanupIfCleared(netKey);
        if (MASTERS.get(netKey).get() != null)
            return Objects.requireNonNull(MASTERS.get(netKey).get()).getData().get();
        return null;
    }

    private static void cleanupIfCleared(NetKey netKey) {
        var ref = MASTERS.get(netKey);
        if (ref != null && ref.get() == null) {
            MASTERS.remove(netKey);
            MASTERS_COUNT.remove(netKey);
        }
    }
}
