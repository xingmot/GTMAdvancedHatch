package com.xingmot.gtmadvancedhatch.common.machines.adaptivehatch;

import com.xingmot.gtmadvancedhatch.api.NoConsumeNotifiabbleEnergyContainer;
import com.xingmot.gtmadvancedhatch.api.adaptivenet.*;
import com.xingmot.gtmadvancedhatch.common.AHItems;
import com.xingmot.gtmadvancedhatch.common.data.MachinesConstants;
import com.xingmot.gtmadvancedhatch.common.data.TagConstants;
import com.xingmot.gtmadvancedhatch.common.machines.NetEnergyHatchPartMachine;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.common.data.GTItems;

import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.hepdd.gtmthings.api.misc.WirelessEnergyManager;
import com.hepdd.gtmthings.utils.TeamUtil;
import lombok.Getter;
import lombok.Setter;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdaptiveNetEnergyHatchPartMachine extends NetEnergyHatchPartMachine implements IFrequency, INetEndpoint {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(AdaptiveNetEnergyHatchPartMachine.class, NetEnergyHatchPartMachine.MANAGED_FIELD_HOLDER);
    // 判断是否可以连接
    @Persisted
    protected boolean isConnect;
    // 适配网络uuid
    @Persisted
    @Getter
    protected UUID net_uuid = MachinesConstants.UUID_ZERO;
    // 适配网络玩家/队伍(仅用于显示）
    @Persisted
    @Getter
    @Setter
    protected String name;
    // 适配网络频率
    @Getter
    @Persisted
    protected long frequency = 0L;
    AdaptiveSlave adaptiveSlave;
    private long maxEnergy = 0L;
    @Persisted
    private long voltage = 0L;
    @Persisted
    private int amps = 1;
    @Persisted
    private int setTier = 0;
    @Persisted
    public final NoConsumeNotifiabbleEnergyContainer energyContainer;

    private TickableSubscription updEnergySubs;
    private TickableSubscription updNet;

    public AdaptiveNetEnergyHatchPartMachine(IMachineBlockEntity holder, IO io) {
        super(holder, 0, io, 1);
        this.adaptiveSlave = new AdaptiveSlave(this, AdaptiveConstants.NET_TYPE_ENERGY);
        if (io == IO.OUT) {
            this.energyContainer = NoConsumeNotifiabbleEnergyContainer.emitterContainer(this, this.maxEnergy, this.voltage, this.amps);
        } else {
            this.energyContainer = NoConsumeNotifiabbleEnergyContainer.receiverContainer(this, this.maxEnergy, this.voltage, this.amps);
        }
    }

    protected void reset() {
        this.voltage = 0L;
        this.amps = 1;
        this.setTier = 0;
        this.maxEnergy = 0L;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    // =============================== 电网仓更新逻辑、自适配更新逻辑 ==================================
    private void updateSubscription() {
        if (super.owner_uuid != null) {
            this.updEnergySubs = this.subscribeServerTick(this.updEnergySubs, this::updateEnergy);
        } else if (this.updEnergySubs != null) {
            this.updEnergySubs.unsubscribe();
            this.updEnergySubs = null;
        }
        if (frequency != 0L && this.isConnect) {
            this.updNet = this.subscribeServerTick(this.updNet, this::updateNet);
        } else if (this.updNet != null) {
            this.updNet.unsubscribe();
            this.updNet = null;
        }
    }

    private void updateNet() {
        if (this.frequency != 0L && this.net_uuid != null && !LDLib.isRemote()) {
            this.adaptiveSlave.updateStatus();
        }
    }

    // 配方会默认直接拉电网，但这两个方法仍然可以兜底
    private void useEnergy() {
        long currentStored = this.energyContainer.getEnergyStored();
        long maxStored = this.energyContainer.getEnergyCapacity();
        long changeStored = Math.min(maxStored - currentStored, this.energyContainer.getInputVoltage() * this.energyContainer.getInputAmperage());
        if (currentStored != maxStored && WirelessEnergyManager.addEUToGlobalEnergyMap(this.owner_uuid, -changeStored, this)) {
            this.energyContainer.setEnergyStored(maxStored);
        }
    }

    private void addEnergy() {
        long currentStored = this.energyContainer.getEnergyStored();
        if (currentStored != 0L) {
            WirelessEnergyManager.addEUToGlobalEnergyMap(this.owner_uuid, currentStored, this);
            this.energyContainer.setEnergyStored(0L);
        }
    }

    private void updateEnergy() {
        if (super.owner_uuid != null) {
            if (this.energyContainer.owner_uuid == null) {
                this.energyContainer.owner_uuid = this.owner_uuid;
            }
            if (this.io == IO.IN) {
                this.useEnergy();
            } else {
                this.addEnergy();
            }
        }
    }

    // =============================== IInteractedMachine ==================================
    /** 内存与网络配置内存交互逻辑 */
    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack is = player.getItemInHand(hand);
        if (!is.isEmpty())
            if (is.is(GTItems.TOOL_DATA_STICK.asItem())) {
                this.owner_uuid = player.getUUID();
                this.energyContainer.owner_uuid = player.getUUID();
                if (LDLib.isRemote()) player.sendSystemMessage(Component.translatable("gtmthings.machine.wireless_energy_hatch.tooltip.bind", new Object[] { TeamUtil.GetName(player) }));
                this.updateSubscription();
                return InteractionResult.SUCCESS;
                // 网络配置内存
            } else if (is.is(AHItems.TOOL_NET_DATA_STICK.asItem())) {
                try {
                    if (is.getTag() != null) {
                        this.net_uuid = is.getTag().getUUID(TagConstants.ADAPTIVE_NET_UUID);
                        this.name = is.getTag().getString(TagConstants.ADAPTIVE_NET_NAME);
                        this.frequency = is.getTag().getLong(TagConstants.ADAPTIVE_NET_FREQUENCY);
                    }
                    setConnect(adaptiveSlave.setUUIDAndFrequency(this.net_uuid, this.frequency));
                    if (LDLib.isRemote()) player.displayClientMessage(Component.translatable("gtmadvancedhatch.machine.adaptivee.export_data", name), true);
                    return InteractionResult.SUCCESS;
                } catch (Exception ignored) {}
            }
        return InteractionResult.PASS;
    }

    @Override
    public boolean onLeftClick(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
        ItemStack is = player.getItemInHand(hand);
        if (is.isEmpty()) {
            return false;
        } else if (is.is(GTItems.TOOL_DATA_STICK.asItem())) {
            this.owner_uuid = null;
            this.energyContainer.owner_uuid = null;
            if (LDLib.isRemote()) {
                player.sendSystemMessage(Component.translatable("gtmthings.machine.wireless_energy_hatch.tooltip.unbind"));
            }
            this.updateSubscription();
            return true;
        } else if (is.is(AHItems.TOOL_NET_DATA_STICK.asItem())) {
            setNetUUID(MachinesConstants.UUID_ZERO);
            setFrequency(0L);
            if (LDLib.isRemote()) {
                player.sendSystemMessage(Component.translatable("gtmadvancedhatch.machine.adaptivee.clear_data"));
            }
            return true;
        }
        return false;
    }

    @Override
    public int getTier() {
        return this.setTier;
    }

    // 负责动态更新机器状态，这里主要是刷新电压电流
    // TODO 机器电压电流变化时，JADE不能及时更新显示信息，此处标记一个需要mixin的todo
    private void updateMultiMachine() {
        Level level = this.getLevel();
        for (BlockPos pos : this.controllerPositions) {
            if (level instanceof ServerLevel && level.isLoaded(pos)) {
                MetaMachine machine = MetaMachine.getMachine(level, pos);
                if (machine instanceof IMultiController controller) {
                    controller.onPartUnload();
                    controller.onStructureFormed();
                }
            }
        }
    }

    // =============================== IMachineLife ==================================
    @Override
    public void onMachinePlaced(@Nullable LivingEntity player, ItemStack stack) {
        if (player != null) {
            UUID uuid = TeamUtil.getTeamUUID(player.getUUID());
            setUUID(uuid);
            energyContainer.setOwner_uuid(uuid);
            this.updateSubscription();
        }
    }

    public void onLoad() {
        super.onLoad();
        this.updateSubscription();
        if (getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new TickTask(0, () -> {
                // 在首个tick进行加载
                adaptiveSlave.setUUIDAndFrequency(this.net_uuid, this.frequency);
            }));
        }
    }

    // =============================== IBindable ==================================
    @Override
    public UUID getUUID() {
        return this.owner_uuid;
    }

    @Override
    public void setUUID(UUID uuid) {
        this.owner_uuid = uuid;
    }

    public void setNetUUID(UUID uuid) {
        this.net_uuid = uuid;
        setConnect(adaptiveSlave.setUUIDAndFrequency(this.net_uuid, this.frequency));
    }

    @Override
    public void setFrequency(long frequency) {
        this.frequency = frequency;
        setConnect(adaptiveSlave.setUUIDAndFrequency(this.net_uuid, this.frequency));
    }

    public void setConnect(boolean connect) {
        this.isConnect = connect;
        if (isConnect) {
            updateMultiMachine();
            this.updateSubscription();
            return;
        }
        // 否则重设为无效状态
        reset();
    }

    // =============================== INetEndpoint ==================================
    @Override
    public @Nullable ServerLevel getServerLevel() {
        Level lvl = this.holder.getSelf().getLevel();
        return lvl instanceof ServerLevel sl ? sl : null;
    }

    @Override
    public BlockPos getBlockPos() {
        return this.holder.getSelf().getBlockPos();
    }

    @Override
    @Nullable
    public Supplier<? extends CompoundTag> getData() {
        return null;
    }

    /** 能源仓是第二个槽位的数据 */
    @Override
    public void encodeData(CompoundTag tag) {
        String s_tag = io == IO.OUT ? "data0" : "data1";
        AdaptiveNetEnergyTerminal.AdaptiveData adaptiveData = AdaptiveNetEnergyTerminal.AdaptiveData.fromTag((CompoundTag) tag.get(s_tag));
        if (this.maxEnergy != adaptiveData.maxEnergy || this.amps != adaptiveData.amps ||
                this.voltage != adaptiveData.voltage || this.setTier != adaptiveData.setTier) {
            this.maxEnergy = adaptiveData.maxEnergy;
            this.amps = adaptiveData.amps;
            this.voltage = adaptiveData.voltage;
            this.setTier = adaptiveData.setTier;
            this.energyContainer.resetBasicInfo(this.maxEnergy, this.voltage, this.amps, 0L, 0L);
            updateMultiMachine();
        }
    }

    @Override
    public boolean isEndpointRemoved() {
        return this.holder.getSelf().isRemoved();
    }
}
