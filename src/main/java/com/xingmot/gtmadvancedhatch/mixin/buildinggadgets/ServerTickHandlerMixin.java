package com.xingmot.gtmadvancedhatch.mixin.buildinggadgets;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

import java.util.*;

import com.direwolf20.buildinggadgets2.common.events.ServerBuildList;
import com.direwolf20.buildinggadgets2.common.events.ServerTickHandler;
import com.direwolf20.buildinggadgets2.common.worlddata.BG2Data;
import com.direwolf20.buildinggadgets2.util.GadgetNBT;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = ServerTickHandler.class, remap = false)
public class ServerTickHandlerMixin {

    @Final
    @Shadow
    public static final HashMap<UUID, ServerBuildList> buildMap = new HashMap<>();

    /** 防止一次粘贴后就清除数据 */
    @Inject(remap = false, method = "removeEmptyLists", at = @At("HEAD"), cancellable = true)
    private static void removeEmptyListsMixin(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        Iterator<Map.Entry<UUID, ServerBuildList>> iterator = buildMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, ServerBuildList> entry = iterator.next();
            ServerBuildList serverBuildList = (ServerBuildList) entry.getValue();
            if (((ServerBuildList) entry.getValue()).statePosList.isEmpty()) {
                Player player = event.getServer().getPlayerList().getPlayer(serverBuildList.playerUUID);
                if (serverBuildList.teData != null && !serverBuildList.buildType.equals(ServerBuildList.BuildType.CUT) && player != null && !serverBuildList.buildType.equals(ServerBuildList.BuildType.BUILD)) {
                    BG2Data bg2Data = BG2Data.get(((MinecraftServer) Objects.requireNonNull(serverBuildList.level.getServer())).overworld());
                    bg2Data.getCopyPasteList(GadgetNBT.getUUID(serverBuildList.gadget), true);
                    bg2Data.popUndoList(GadgetNBT.getUUID(serverBuildList.gadget));
                    bg2Data.getTEMap(GadgetNBT.getUUID(serverBuildList.gadget));
                }

                iterator.remove();
            }
        }
        ci.cancel();
    }
}
