package com.xingmot.gtmadvancedhatch.api.adaptivenet;

import net.minecraft.nbt.CompoundTag;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;

import static com.xingmot.gtmadvancedhatch.api.adaptivenet.AdaptiveConstants.NET_TYPE_EMPTY;

import joptsimple.internal.Strings;
import org.jetbrains.annotations.Nullable;

/**
 * 参考自 ExtendedAE Plus by.GaLicn
 * - 修改0频率也能注册，方便统计数量
 */
public final class NetMasterRegistry {

    private NetMasterRegistry() {}

    private static final Map<Key, WeakReference<INetEndpoint>> MASTERS = new HashMap<>();
    private static final Map<Key, Long> MASTERS_COUNT = new HashMap<>();

    public static synchronized boolean register(@Nonnull String type, long frequency, @Nullable UUID uuid, INetEndpoint endpoint) {
        if (frequency == 0) return false;
        final Key key = new Key(type, frequency, uuid);

        cleanupIfCleared(key);
        var existing = MASTERS.get(key);
        var existingVal = existing == null ? null : existing.get();
        if (existingVal != null && !existingVal.isEndpointRemoved()) {
            // 同维度同频率同所有者已经有主端
            return false;
        }
        MASTERS.put(key, new WeakReference<>(endpoint));
        MASTERS_COUNT.put(key, 0L);
        return true;
    }

    public static synchronized void unregister(@Nonnull String type, long frequency, @Nullable UUID uuid, INetEndpoint endpoint) {
        if (frequency == 0) return;
        final Key key = new Key(type, frequency, uuid);

        var ref = MASTERS.get(key);
        if (ref != null) {
            var cur = ref.get();
            if (cur == null || cur == endpoint) {
                MASTERS.remove(key);
                MASTERS_COUNT.remove(key);
            }
        }
    }

    public static synchronized INetEndpoint get(@Nonnull String type, long frequency, @Nullable UUID uuid) {
        if (frequency == 0) return null;
        final Key key = new Key(type, frequency, uuid);

        cleanupIfCleared(key);
        var ref = MASTERS.get(key);
        return ref == null ? null : ref.get();
    }

    public static synchronized Long getCount(@Nonnull String type, long frequency, @Nullable UUID uuid) {
        if (frequency == 0) return 0L;
        final Key key = new Key(type, frequency, uuid);

        cleanupIfCleared(key);
        return MASTERS_COUNT.get(key);
    }

    public static synchronized void addCount(@Nonnull String type, long frequency, @Nullable UUID uuid, int i) {
        if (frequency == 0) return;
        final Key key = new Key(type, frequency, uuid);

        cleanupIfCleared(key);
        MASTERS_COUNT.put(key, MASTERS_COUNT.get(key) + i);
    }

    /** 主要是方便终端和适配仓一起用 */
    public static synchronized CompoundTag getData(@Nonnull String type, long frequency, @Nullable UUID uuid) {
        if (frequency == 0) return null;
        final Key key = new Key(type, frequency, uuid);

        cleanupIfCleared(key);
        if (MASTERS.get(key).get() != null)
            return Objects.requireNonNull(MASTERS.get(key).get()).getData().get();
        return null;
    }

    private static void cleanupIfCleared(Key key) {
        var ref = MASTERS.get(key);
        if (ref != null && ref.get() == null) {
            MASTERS.remove(key);
            MASTERS_COUNT.remove(key);
        }
    }

    private record Key(@Nullable String type, long freq, UUID owner) {

        @Override
        public String toString() {
            return (Strings.isNullOrEmpty(type) ? NET_TYPE_EMPTY : type) + "#" + freq + "@" + owner;
        }
    }
}
