package com.xingmot.gtmadvancedhatch.mixin.gtmt;

import com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor.NNewWirelessEnergyMonitor;
import com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor.NewWirelessEnergyMonitor;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;

import java.util.function.Function;

import com.hepdd.gtmthings.data.WirelessMachines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WirelessMachines.class)
public class WirelessMachinesMixin {

    @ModifyArg(remap = false,
               method = "<clinit>",
               at = @At(value = "INVOKE",
                        target = "Lcom/gregtechceu/gtceu/api/registry/registrate/GTRegistrate;machine(Ljava/lang/String;Ljava/util/function/Function;)Lcom/gregtechceu/gtceu/api/registry/registrate/MachineBuilder;"),
               index = 1)
    private static Function<IMachineBlockEntity, MetaMachine> StaticMixin(Function<IMachineBlockEntity, MetaMachine> metaMachine) {
        return NNewWirelessEnergyMonitor::new;
    }
}
