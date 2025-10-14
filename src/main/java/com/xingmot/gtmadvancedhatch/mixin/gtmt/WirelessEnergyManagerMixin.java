package com.xingmot.gtmadvancedhatch.mixin.gtmt;

import com.xingmot.gtmadvancedhatch.api.util.MachineDataStorage;
import com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor.EnergyStat;

import com.gregtechceu.gtceu.api.machine.MetaMachine;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

import com.hepdd.gtmthings.api.misc.GlobalVariableStorage;
import com.hepdd.gtmthings.api.misc.WirelessEnergyManager;
import com.hepdd.gtmthings.data.WirelessEnergySavaedData;
import com.hepdd.gtmthings.utils.TeamUtil;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WirelessEnergyManager.class)
public class WirelessEnergyManagerMixin {

    @Inject(remap = false, method = "addEUToGlobalEnergyMap(Ljava/util/UUID;Ljava/math/BigInteger;Lcom/gregtechceu/gtceu/api/machine/MetaMachine;)Z", at = @At(value = "HEAD"), cancellable = true)
    private static void addEUToGlobalEnergyMap(UUID user_uuid, BigInteger EU, MetaMachine machine, CallbackInfoReturnable<Boolean> cir) {
        try {
            WirelessEnergySavaedData.INSTANCE.setDirty(true);
        } catch (Exception exception) {
            System.out.println("COULD NOT MARK GLOBAL ENERGY AS DIRTY IN ADD EU");
            exception.printStackTrace();
        }

        UUID teamUUID = TeamUtil.getTeamUUID(user_uuid);
        MachineDataStorage.put(Pair.of(user_uuid, machine), new BigDecimal(EU));
        BigInteger totalEU = (BigInteger) GlobalVariableStorage.GlobalEnergy.getOrDefault(teamUUID, BigInteger.ZERO);
        if (machine != null) {
            EnergyStat.createOrgetEnergyStat(user_uuid).update(EU);
        }
        if (totalEU.signum() < 0) {
            totalEU = BigInteger.ZERO;
            GlobalVariableStorage.GlobalEnergy.put(TeamUtil.getTeamUUID(user_uuid), totalEU);
        }

        totalEU = totalEU.add(EU);
        if (totalEU.signum() >= 0) {
            GlobalVariableStorage.GlobalEnergy.put(teamUUID, totalEU);
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
        cir.cancel();
    }
}
