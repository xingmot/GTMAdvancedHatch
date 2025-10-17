package com.xingmot.gtmadvancedhatch.api.adaptivenet;

import java.util.UUID;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

/**
 * 参考自 ExtendedAE Plus by.GaLicn
 * - 添加一个type，给不同的自适配系统类型作区分，便于拓展
 */
public class AdaptiveSlave {

    private final INetEndpoint host;
    private INetEndpoint master;
    @Getter
    private long frequency; // 0 未设置
    @Nullable
    @Setter
    private UUID uuid; // 放置者UUID
    private String type;
    private boolean shutdown = true;

    public AdaptiveSlave(@Nonnull INetEndpoint host, @Nonnull String type) {
        this.host = host;
        this.type = type;
    }

    /**
     * @return 成功建立连接为true
     */
    public boolean setUUIDAndFrequency(UUID uuid, long frequency) {
        if (this.uuid != uuid || this.frequency != frequency) {
            this.uuid = uuid;
            this.frequency = frequency;
            // uuid或频率变更，立即尝试重连/断开
            updateStatus();
            return !shutdown;
        }
        return false;
    }

    public boolean isConnected() {
        return !shutdown;
    }

    /**
     * 建议在 BE 的 serverTick 或者频率/加载状态变化时调用。
     * 查找终端，通过encodeData方法传递数据
     */
    public void updateStatus() {
        if (host.isEndpointRemoved() || frequency == 0) return;
        // 执行一次同步，返回值为true表示是迁移模式，此时不会重复执行同步操作。
        // 否则表示普通模式，会正常搜寻主网。
        if (!syncData()) {
            // placerId可以为null（公共收发器模式）
            this.master = NetMasterRegistry.get(type, frequency, uuid);
            syncData();
        }
    }

    private boolean syncData() {
        boolean flag = false;
        shutdown = false;
        // 无主或主端不可用
        shutdown = master == null || master.isEndpointRemoved();
        if (!shutdown)
            flag = this.host.encodeData(master.getData().get());
        return flag;
    }
}
