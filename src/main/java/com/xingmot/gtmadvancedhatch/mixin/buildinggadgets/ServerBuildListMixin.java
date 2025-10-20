package com.xingmot.gtmadvancedhatch.mixin.buildinggadgets;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;

import com.direwolf20.buildinggadgets2.common.events.ServerBuildList;
import com.direwolf20.buildinggadgets2.util.datatypes.TagPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = ServerBuildList.class, remap = false)
public class ServerBuildListMixin {

    @Shadow
    public ArrayList<TagPos> teData;
    @Shadow
    public BlockPos lookingAt;

    @Inject(remap = false, method = "getTagForPos", at = @At("HEAD"), cancellable = true)
    private void getTagForPosMixin(BlockPos pos, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag compoundTag = new CompoundTag();
        if (this.teData != null && !this.teData.isEmpty()) {
            BlockPos blockPos = pos.subtract(this.lookingAt);

            for (TagPos data : this.teData) {
                if (data.pos.equals(blockPos)) {
                    compoundTag = data.tag;
                    break;
                }
            }
            cir.setReturnValue(compoundTag);
        } else {
            cir.setReturnValue(compoundTag);
        }
    }
}
