package com.xingmot.gtmadvancedhatch.integration.jade;

import com.xingmot.gtmadvancedhatch.GTMAdvancedHatch;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class GTJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new NetEnergyProvider(), MetaMachineBlockEntity.class);
        registration.registerBlockDataProvider(new AdaptiveNetProvider(), MetaMachineBlockEntity.class);
        registration.registerBlockDataProvider(new BatchableProvider(), MetaMachineBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new NetEnergyProvider(), MetaMachineBlock.class);
        registration.registerBlockComponent(new AdaptiveNetProvider(), MetaMachineBlock.class);
        registration.registerBlockComponent(new BatchableProvider(), MetaMachineBlock.class);

        /* jade config */
        registration.addConfig(GTMAdvancedHatch.id("adaptive_net_provider.show_uuid"), false);
    }
}
