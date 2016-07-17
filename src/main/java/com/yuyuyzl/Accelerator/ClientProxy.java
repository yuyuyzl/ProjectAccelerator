package com.yuyuyzl.Accelerator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by user on 2016/5/28.
 */
public class ClientProxy extends CommonProxy{
    public void preInit(){
        super.preInit();
        Item itemblockmain=GameRegistry.findItem("acceleratormod","Acc_MainBlock");
        ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation("acceleratormod:Acc_MainBlock", "inventory");
        Item itemblockhull=GameRegistry.findItem("acceleratormod","Acc_Hull");
        ModelResourceLocation itemModelResourceLocation1 = new ModelResourceLocation("acceleratormod:Acc_Hull", "inventory");
        Item itemblockenergy=GameRegistry.findItem("acceleratormod","Acc_Energy");
        ModelResourceLocation itemModelResourceLocation2 = new ModelResourceLocation("acceleratormod:Acc_Energy", "inventory");
        Item itemblockfluid=GameRegistry.findItem("acceleratormod","Acc_Fluid");
        ModelResourceLocation itemModelResourceLocation3 = new ModelResourceLocation("acceleratormod:Acc_Fluid", "inventory");
        ModelLoader.setCustomModelResourceLocation(itemblockmain,0,itemModelResourceLocation);
        ModelLoader.setCustomModelResourceLocation(itemblockhull,0,itemModelResourceLocation1);
        ModelLoader.setCustomModelResourceLocation(itemblockenergy,0,itemModelResourceLocation2);
        ModelLoader.setCustomModelResourceLocation(itemblockfluid,0,itemModelResourceLocation3);
        Config.clientPreInit();
}
    public void init(){
        super.init();
    }
    public void postInit(){
        super.postInit();
    }
    @Override
    public boolean playerIsInCreativeMode(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)player;
            return entityPlayerMP.isCreative();
        } else if (player instanceof EntityPlayerSP) {
            return Minecraft.getMinecraft().playerController.isInCreativeMode();
        }
        return false;
    }
}
