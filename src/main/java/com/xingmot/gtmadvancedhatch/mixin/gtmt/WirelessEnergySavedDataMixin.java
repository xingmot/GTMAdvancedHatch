package com.xingmot.gtmadvancedhatch.mixin.gtmt;

import com.xingmot.gtmadvancedhatch.integration.gtmt.newmonitor.EnergyStat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;

import java.math.BigInteger;

import com.hepdd.gtmthings.api.misc.GlobalVariableStorage;
import com.hepdd.gtmthings.data.WirelessEnergySavaedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WirelessEnergySavaedData.class)
public class WirelessEnergySavedDataMixin {

    @Inject(remap = false,
            method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At(value = "TAIL", target = "Lcom/hepdd/gtmthings/data/WirelessEnergySavaedData;<init>(Lnet/minecraft/server/level/ServerLevel;)V"),
            cancellable = true)
    public void WirelessEnergySavaedDataMixin(ServerLevel serverLevel, CompoundTag tag, CallbackInfo ci) {
        ListTag allEnergy = tag.getList("allEnergy", 10);

        for (int i = 0; i < allEnergy.size(); ++i) {
            CompoundTag engTag = allEnergy.getCompound(i);
            GlobalVariableStorage.GlobalEnergy.put(engTag.getUUID("uuid"), new BigInteger(engTag.getString("energy").isEmpty() ? "0" : engTag.getString("energy")));
            EnergyStat.GlobalEnergyStat.put(engTag.getUUID("uuid"), new EnergyStat(serverLevel.getServer().getTickCount()));
        }

        ci.cancel();
    }
}
