package com.yuyuyzl.Accelerator;

import com.yuyuyzl.Accelerator.Containers.ContainerAccMain;
import com.yuyuyzl.Accelerator.Guis.GuiAccMain;
import com.yuyuyzl.Accelerator.TileEntities.TEAccMain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.util.HashMap;

/**
 * Created by user on 2016/5/29.
 */
public class GUIHandler implements IGuiHandler{

    private static GUIHandler instance=new GUIHandler();
    public static GUIHandler getInstance(){return instance;}
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID){
            case 0:return new ContainerAccMain(player.inventory,(TEAccMain)world.getTileEntity(new BlockPos(x,y,z)));

        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID){
            case 0:return new GuiAccMain(player.inventory,(TEAccMain)world.getTileEntity(new BlockPos(x,y,z)));

        }
        return null;
    }
}
