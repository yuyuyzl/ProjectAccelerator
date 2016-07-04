package com.yuyuyzl.Accelerator;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AcceleratorMod.MODID, version = AcceleratorMod.VERSION)
public class AcceleratorMod
{
    public static final String MODID = "acceleratormod";
    public static final String VERSION = "0.01";

    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="com.yuyuyzl.Accelerator.ClientProxy",serverSide = "com.yuyuyzl.Accelerator.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }
    @Mod.Instance(AcceleratorMod.MODID)
    public static AcceleratorMod instance;
}
