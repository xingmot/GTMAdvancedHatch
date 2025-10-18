package com.xingmot.gtmadvancedhatch.mixin.buildinggadgets;

import com.direwolf20.buildinggadgets2.common.blockentities.RenderBlockBE;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RenderBlockBE.class)
public abstract class RenderBlockBEMixin extends BlockEntity {
    @Unique
    boolean gtmadvancedhatch$isCut = true;

    @Unique
    public void gtmadvancedhatch$isCut(boolean isCut) {
        this.gtmadvancedhatch$isCut = isCut;
    }

    @Shadow
    public CompoundTag blockEntityData;

    @Shadow
    public abstract void setBlockEntityData(CompoundTag tag);

    public RenderBlockBEMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    /** 确保电网仓在tag载入后能立即再调用一次load方法 */
    @Inject(remap = false, method = "setRealBlock", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/BlockEntity;load(Lnet/minecraft/nbt/CompoundTag;)V",
            shift = At.Shift.AFTER),locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setRealBlockMixin(BlockState realBlock, CallbackInfo ci, BlockState adjustedState, BlockEntity newBE) {
        newBE.clearRemoved();
    }
}
