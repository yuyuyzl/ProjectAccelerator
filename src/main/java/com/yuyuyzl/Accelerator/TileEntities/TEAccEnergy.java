package com.yuyuyzl.Accelerator.TileEntities;

import ic2.api.energy.prefab.BasicSink;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;

/**
 * Created by user on 2016/7/5.
 */
public class TEAccEnergy extends TileEntity implements ITickable {
    private BasicSink ic2EnergySink = new BasicSink(this,0,5);
    public boolean isOn=false;
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ic2EnergySink.writeToNBT(compound);
        compound.setBoolean("ison",isOn);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        ic2EnergySink.readFromNBT(compound);
        isOn=compound.getBoolean("ison");
    }

    @Override
    public void update() {
        //ic2EnergySink.
        if (isOn)ic2EnergySink.setCapacity(Integer.MAX_VALUE);else ic2EnergySink.setCapacity(0);
        ic2EnergySink.update();

    }
    public double getEnergyStored(){
        return ic2EnergySink.getEnergyStored();
    }



    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos,0,this.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
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

    @Override
    public void invalidate() {
        ic2EnergySink.invalidate();
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        ic2EnergySink.onChunkUnload();
        super.onChunkUnload();
    }
}
