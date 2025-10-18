package com.xingmot.gtmadvancedhatch.mixin.buildinggadgets;

import com.direwolf20.buildinggadgets2.common.events.ServerBuildList;
import com.direwolf20.buildinggadgets2.util.datatypes.TagPos;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;

@Mixin(ServerBuildList.class)
public class ServerBuildListMixin {
    @Shadow
    public ArrayList<TagPos> teData;
    @Shadow
    public BlockPos lookingAt;

    @Inject(remap = false, method = "getTagForPos",at = @At("HEAD"), cancellable = true)
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
