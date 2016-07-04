package com.yuyuyzl.Accelerator;

import com.yuyuyzl.Accelerator.Blocks.BlockAccHull;
import com.yuyuyzl.Accelerator.Blocks.BlockAccMain;
import com.yuyuyzl.Accelerator.Network.NetworkHandler;
import com.yuyuyzl.Accelerator.TileEntities.TEAccMain;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by user on 2016/5/28.
 */
public class CommonProxy {
    public Block blockAccMain;
    public Block blockAccHull;
    public void preInit(){
        blockAccMain=new BlockAccMain().setUnlocalizedName("Acc_MainBlock");
        GameRegistry.registerBlock(blockAccMain,"Acc_MainBlock");
        blockAccHull=new BlockAccHull().setUnlocalizedName("Acc_Hull");
        GameRegistry.registerBlock(blockAccHull,"Acc_Hull");
        GameRegistry.registerTileEntity(TEAccMain.class,"Acc_MainBlock");
        NetworkRegistry.INSTANCE.registerGuiHandler(AcceleratorMod.instance,GUIHandler.getInstance());
        NetworkHandler.init();
    }
    public void init(){

    }
    public void postInit(){

    }
    public boolean playerIsInCreativeMode(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
            return entityPlayerMP.theItemInWorldManager.isCreative();
        }
        return false;
    }
}
