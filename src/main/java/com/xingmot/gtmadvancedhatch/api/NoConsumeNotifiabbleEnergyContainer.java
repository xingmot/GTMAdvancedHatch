package com.xingmot.gtmadvancedhatch.api;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.hepdd.gtmthings.api.misc.WirelessEnergyManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public class NoConsumeNotifiabbleEnergyContainer extends NotifiableEnergyContainer {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(NoConsumeNotifiabbleEnergyContainer.class, NotifiableRecipeHandlerTrait.MANAGED_FIELD_HOLDER);

    @Setter
    @Getter
    @Persisted
    public UUID owner_uuid;

    public NoConsumeNotifiabbleEnergyContainer(MetaMachine machine, long maxCapacity, long maxInputVoltage, long maxInputAmperage, long maxOutputVoltage, long maxOutputAmperage) {
        super(machine, maxCapacity, maxInputVoltage, maxInputAmperage, maxOutputVoltage, maxOutputAmperage);
    }

    public static NoConsumeNotifiabbleEnergyContainer emitterContainer(MetaMachine machine, long maxCapacity, long maxOutputVoltage, long maxOutputAmperage) {
        return new NoConsumeNotifiabbleEnergyContainer(machine, maxCapacity, 0L, 0L, maxOutputVoltage, maxOutputAmperage);
    }

    public static NoConsumeNotifiabbleEnergyContainer receiverContainer(MetaMachine machine, long maxCapacity, long maxInputVoltage, long maxInputAmperage) {
        return new NoConsumeNotifiabbleEnergyContainer(machine, maxCapacity, maxInputVoltage, maxInputAmperage, 0L, 0L);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    // 把实际操作能量的部分改为操作电网
    @Override
    public List<Long> handleRecipeInner(IO io, GTRecipe recipe, List<Long> left, @Nullable String slotName, boolean simulate) {
        long sum = (Long) left.stream().reduce(0L, Long::sum);
        if (io == IO.IN) {
            long canOutput = this.getEnergyStored();
            long output = Math.min(canOutput, sum);
            if (!simulate) {
                if (owner_uuid == null || !WirelessEnergyManager.addEUToGlobalEnergyMap(this.owner_uuid, -output, this.getMachine())) {
                    this.addEnergy(-output);
                }
            }
            sum -= canOutput;
        } else if (io == IO.OUT) {
            long canInput = this.getEnergyCapacity() - this.getEnergyStored();
            if (!simulate) {
                if (owner_uuid == null || !WirelessEnergyManager.addEUToGlobalEnergyMap(this.owner_uuid, Math.min(canInput, sum), this.getMachine()))
                    this.addEnergy(Math.min(canInput, sum));
            }
            sum -= canInput;
        }

        return sum <= 0L ? null : Collections.singletonList(sum);
    }
}
