package com.xingmot.gtmadvancedhatch.integration.jade;

import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;
import com.xingmot.gtmadvancedhatch.common.machines.NetEnergyHatchPartMachine;
import com.xingmot.gtmadvancedhatch.common.machines.NetLaserHatchPartMachine;
import com.xingmot.gtmadvancedhatch.util.copy.NumberUtils;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

import com.hepdd.gtmthings.api.capability.IBindable;
import com.hepdd.gtmthings.api.misc.WirelessEnergyManager;
import com.hepdd.gtmthings.common.block.machine.multiblock.part.WirelessEnergyHatchPartMachine;
import com.hepdd.gtmthings.common.block.machine.multiblock.part.WirelessLaserHatchPartMachine;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class NetEnergyProvider extends CapabilityBlockProvider<IBindable> {

    protected NetEnergyProvider() {
        super(GTMAdvancedHatch.id("net_energy_provider"));
    }

    @Override
    protected @Nullable IBindable getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        if (level.getBlockEntity(pos) instanceof MetaMachineBlockEntity metaMachineBlockEntity) {
            var metaMachine = metaMachineBlockEntity.getMetaMachine();
            if (metaMachine instanceof WirelessEnergyHatchPartMachine we && we.owner_uuid != null) {
                UUID uuid = we.owner_uuid;
                return new IBindable() {

                    @Override
                    public UUID getUUID() {
                        return uuid;
                    }

                    @Override
                    public void setUUID(UUID uuid1) {}
                };
            } else if (metaMachine instanceof WirelessLaserHatchPartMachine wl && wl.owner_uuid != null) {
                UUID uuid = wl.owner_uuid;
                return new IBindable() {

                    @Override
                    public UUID getUUID() {
                        return uuid;
                    }

                    @Override
                    public void setUUID(UUID uuid1) {}
                };
            }
        }
        return null;
    }

    @Override
    protected void write(CompoundTag data, IBindable capability) {
        if (capability.getUUID() != null) {
            data.putUUID("uuid", capability.getUUID());
            data.putString("energy", NumberUtils.formatBigIntegerNumberOrSic(WirelessEnergyManager.getUserEU(capability.getUUID())));
        }
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
                              BlockEntity blockEntity, IPluginConfig config) {
        if (!(blockEntity instanceof MetaMachineBlockEntity metaMachineBlockEntity)) return;
        var metaMachine = metaMachineBlockEntity.getMetaMachine();
        if (metaMachine instanceof NetEnergyHatchPartMachine || metaMachine instanceof NetLaserHatchPartMachine) {
            if (capData.hasUUID("uuid")) {
                UUID uuid = capData.getUUID("uuid");
                WirelessEnergyManager.strongCheckOrAddUser(uuid);
                tooltip.add(Component.translatable("gtmadvancedhatch.machine.net_energy_stored.tooltip",
                        capData.getString("energy")));
            }
        }
    }
}
