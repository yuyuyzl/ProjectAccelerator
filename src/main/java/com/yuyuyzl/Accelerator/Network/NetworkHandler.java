package com.yuyuyzl.Accelerator.Network;

import com.yuyuyzl.Accelerator.AcceleratorMod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by user on 2016/5/30.
 */
public class NetworkHandler {
    public static SimpleNetworkWrapper INSTANCE;
    public static void init(){
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(AcceleratorMod.MODID);
        INSTANCE.registerMessage(MessageExplode.class,MessageExplode.class,0, Side.SERVER);
        INSTANCE.registerMessage(MessageScan.class,MessageScan.class,1,Side.SERVER);
    }
}
