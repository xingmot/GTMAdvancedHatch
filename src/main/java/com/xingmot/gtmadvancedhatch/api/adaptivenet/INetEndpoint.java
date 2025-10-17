package com.xingmot.gtmadvancedhatch.api.adaptivenet;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

import java.util.function.Supplier;

/**
 * 参考自 ExtendedAE Plus by.GaLicn
 * - 适配网络端点最小接口。
 * - 获取世界、位置
 * - 通过传递和解析tag主端可以被同频段已注册的另一个主端覆盖配置
 */
public interface INetEndpoint {

    /** 返回方块所在的服务端世界（避免与 BlockEntity#getLevel 冲突） */
    ServerLevel getServerLevel();

    /** 返回方块位置 */
    BlockPos getBlockPos();

    /** 数据 */
    Supplier<? extends CompoundTag> getData();

    /** @return 是否存在回调数据 */
    boolean encodeData(CompoundTag tag);

    /** 是否已移除/销毁（端点视角），用于在卸载或破坏时停止连接 */
    boolean isEndpointRemoved();
}
