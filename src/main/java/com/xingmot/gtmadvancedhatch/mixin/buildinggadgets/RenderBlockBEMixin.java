package com.xingmot.gtmadvancedhatch.mixin.buildinggadgets;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.direwolf20.buildinggadgets2.common.blockentities.RenderBlockBE;
import com.direwolf20.buildinggadgets2.setup.Registration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = RenderBlockBE.class, remap = false, priority = 900)
public class RenderBlockBEMixin extends BlockEntity {

    public RenderBlockBEMixin(BlockPos pos, BlockState state) {
        super(Registration.RenderBlock_BE.get(), pos, state);
    }

    /** 确保电网仓在tag载入后能立即再调用一次load方法 */
    @Inject(remap = false,
            method = "tickServer",
            at = @At("TAIL"))
    private void tickServerMixin(CallbackInfo ci) {
        if (this.level != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this.getBlockPos());
            if (blockEntity != null)
                blockEntity.clearRemoved();
        }
    }

    // region 奇葩GTL不知道为什么用不了这种写法
    // @Inject(remap = false,
    // method = "setRealBlock",
    // at = @At(value = "INVOKE",
    // target = "Lnet/minecraft/world/level/block/entity/BlockEntity;load(Lnet/minecraft/nbt/CompoundTag;)V",
    // shift = At.Shift.AFTER),
    // locals = LocalCapture.CAPTURE_FAILSOFT)
    // private void setRealBlockMixin(BlockState realBlock, CallbackInfo ci, BlockState adjustedState, BlockEntity
    // newBE) {
    // newBE.clearRemoved();
    // }
    // endregion
}
