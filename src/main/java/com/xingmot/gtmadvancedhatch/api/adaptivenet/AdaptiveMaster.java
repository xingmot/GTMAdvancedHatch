package com.xingmot.gtmadvancedhatch.api.adaptivenet;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

/**
 * 参考自 ExtendedAE Plus by.GaLicn
 * - 改为0频率也要注册，方便统计数量；跨维度，无距离限制
 * - 需要在其他合适的地方调用register和unregister方法
 * - 添加一个type，给不同的自适配系统类型作区分，便于拓展
 */
public class AdaptiveMaster {

    private final INetEndpoint host;
    private final String type;
    @Getter
    private long frequency;
    @Getter
    @Setter
    private boolean registered;
    @Nullable
    private UUID uuid;

    public AdaptiveMaster(INetEndpoint host, String type) {
        this.host = host;
        this.type = type;
    }

    /**
     * @return 若产生新的频段则返回true
     */
    public boolean setUUIDAndFrequency(UUID uuid, long frequency) {
        boolean result = false;
        // 如果uuid或频率发生变化，先撤销旧频率的注册
        if (this.uuid != uuid || this.frequency != frequency) {
            if (registered) {
                unregister();
            }
            this.uuid = uuid;
            this.frequency = frequency;
        }

        // 频率未变的情况下也要校正注册状态：
        // - 注册
        // - 当端点被移除时，确保处于未注册。
        if (!host.isEndpointRemoved()) {
            if (!registered) {
                result = register();
            }
        } else {
            if (registered) {
                unregister();
            }
        }
        return result;
    }

    public boolean register() {
        if (frequency == 0) return false;
        boolean ok = NetMasterRegistry.register(this.type, frequency, uuid, host);
        this.registered = ok;
        return ok;
    }

    public void unregister() {
        if (!registered || frequency == 0) return;
        // placerId可以为null（公共收发器模式）
        NetMasterRegistry.unregister(this.type, frequency, uuid, host);
        registered = false;
    }

    public void onUnloadOrRemove() {
        unregister();
    }
}
