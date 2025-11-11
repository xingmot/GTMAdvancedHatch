package com.xingmot.gtmadvancedhatch.mixin.gtm;

import com.gregtechceu.gtceu.utils.OverlayedFluidHandler;
import com.lowdragmc.lowdraglib.misc.FluidStorage;
import com.lowdragmc.lowdraglib.misc.FluidTransferList;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidStorage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Mixin(OverlayedFluidHandler.class)
public class OverlayeddFluidHandlerMixin {
    @Redirect(remap = false, method = "<init>", at = @At(value = "INVOKE",target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    public boolean initMixin(List<Object> instance, Object e, @NotNull FluidTransferList tank){
        return instance.add(e);
    }

//    public OverlayedFluidHandler(@NotNull FluidTransferList tank) {
//        IntStream var10000 = IntStream.range(0, tank.getTanks());
//        Objects.requireNonNull(tank);
//        FluidStack[] entries = (FluidStack[])var10000.mapToObj(tank::getFluidInTank).toArray((x$0) -> new FluidStack[x$0]);
//
//        for(int i = 0; i < tank.getTanks(); ++i) {
//            FluidStorage storage = new FluidStorage(tank.getTankCapacity(i));
//            storage.setFluid(entries[i]);
//            int finalI = i;
//            storage.setValidator(fluidStack -> tank.isFluidValid(finalI,fluidStack));
//            this.overlayedTanks.add(new OverlayedFluidHandler.OverlayedTank(storage, tank.isFluidValid(i, entries[i])));
//        }
//    }

    @Mixin(targets = "com.gregtechceu.gtceu.utils.OverlayedFluidHandler$OverlayedTank")
    private static class OverlayedTankMixin{
        @Shadow(remap = false)
        @Final
        private IFluidStorage property;

        @Inject(remap = false,method = "tryInsert",at = @At("HEAD"), cancellable = true)
        public void tryInsertMixin(FluidStack fluid, long amount, CallbackInfoReturnable<Long> cir){
            if(!this.property.isFluidValid(fluid))cir.setReturnValue(0L);
        }
    }
}
