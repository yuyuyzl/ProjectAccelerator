package com.yuyuyzl.Accelerator.TileEntities;

import ic2.api.energy.prefab.BasicSink;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

/**
 * Created by user on 2016/7/5.
 */
public class TEAccEnergy extends TileEntity implements ITickable {
    private BasicSink ic2EnergySink = new BasicSink(this,Integer.MAX_VALUE,5);
    public boolean isOn=false;
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ic2EnergySink.writeToNBT(compound);
        compound.setBoolean("ison",isOn);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        ic2EnergySink.readFromNBT(compound);
        isOn=compound.getBoolean("ison");
    }

    @Override
    public void update() {
        ic2EnergySink.update();
    }
    public double getEnergyStored(){
        return ic2EnergySink.getEnergyStored();
    }
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        final int METADATA = 0;
        return new S35PacketUpdateTileEntity(this.pos, METADATA, nbtTagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
    public int getEnergy(int amount){
        if (ic2EnergySink.getEnergyStored()>=amount){
            ic2EnergySink.useEnergy(amount);
            return amount;
        }else {
            int n= (int) Math.round(ic2EnergySink.getEnergyStored());
            ic2EnergySink.useEnergy(ic2EnergySink.getEnergyStored());
            return n;
        }
    }
}
