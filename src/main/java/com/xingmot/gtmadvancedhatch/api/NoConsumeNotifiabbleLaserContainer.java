package com.xingmot.gtmadvancedhatch.api;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableLaserContainer;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.hepdd.gtmthings.api.misc.WirelessEnergyManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public class NoConsumeNotifiabbleLaserContainer extends NotifiableLaserContainer {

    @Setter
    @Getter
    @Persisted
    public UUID owner_uuid;

    public NoConsumeNotifiabbleLaserContainer(MetaMachine machine, long maxCapacity, long maxInputVoltage, long maxInputAmperage, long maxOutputVoltage, long maxOutputAmperage) {
        super(machine, maxCapacity, maxInputVoltage, maxInputAmperage, maxOutputVoltage, maxOutputAmperage);
    }

    public static NoConsumeNotifiabbleLaserContainer emitterContainer(MetaMachine machine, long maxCapacity, long maxOutputVoltage, long maxOutputAmperage) {
        return new NoConsumeNotifiabbleLaserContainer(machine, maxCapacity, 0L, 0L, maxOutputVoltage, maxOutputAmperage);
    }

    public static NoConsumeNotifiabbleLaserContainer receiverContainer(MetaMachine machine, long maxCapacity, long maxInputVoltage, long maxInputAmperage) {
        return new NoConsumeNotifiabbleLaserContainer(machine, maxCapacity, maxInputVoltage, maxInputAmperage, 0L, 0L);
    }

    // 把实际操作能量的部分改为操作电网
    @Override
    public List<Long> handleRecipeInner(IO io, GTRecipe recipe, List<Long> left, @Nullable String slotName, boolean simulate) {
        long sum = (Long) left.stream().reduce(0L, Long::sum);
        if (io == IO.IN) {
            long canOutput = this.getEnergyStored();
            if (!simulate) {
                if (owner_uuid != null) WirelessEnergyManager.addEUToGlobalEnergyMap(this.owner_uuid, -Math.min(canOutput, sum), this.getMachine());
                else this.addEnergy(-Math.min(canOutput, sum));
            }
            sum -= canOutput;
        } else if (io == IO.OUT) {
            long canInput = this.getEnergyCapacity() - this.getEnergyStored();
            if (!simulate) {
                if (owner_uuid != null) WirelessEnergyManager.addEUToGlobalEnergyMap(this.owner_uuid, Math.min(canInput, sum), this.getMachine());
                else this.addEnergy(Math.min(canInput, sum));
            }
            sum -= canInput;
        }

        return sum <= 0L ? null : Collections.singletonList(sum);
    }
}
