package com.yuyuyzl.Accelerator.TileEntities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.*;

/**
 * Created by user on 2016/7/14.
 */
public class TEAccFluid extends TileEntity implements IFluidHandler,ITickable {
    protected FluidTank fluidTank=new FluidTank(1000);
    public boolean isOn=false;

    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        return this.canFill(from, resource.getFluid())?fluidTank.fill(resource, doFill):0;
    }
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        return resource != null && resource.isFluidEqual(fluidTank.getFluid())?(!this.canDrain(from, resource.getFluid())?null:fluidTank.drain(resource.amount, doDrain)):null;
    }

    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return !this.canDrain(from, null)?null:fluidTank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return from==null;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[]{fluidTank.getInfo()};
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.fluidTank.readFromNBT(compound.getCompoundTag("fluidTank"));
        isOn=compound.getBoolean("ison");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.fluidTank.writeToNBT(fluidTankTag);
        compound.setTag("fluidTank", fluidTankTag);
        compound.setBoolean("ison",isOn);
        return compound;
    }
    public int getStorage(){
        return fluidTank.getFluidAmount();
    }
    @Override
    public void update() {
        if (fluidTank.getFluid()==null){
            //ic2EnergySink.useEnergy(EUperUU);
            fluidTank.setFluid(FluidRegistry.getFluidStack("ic2uu_matter",1).copy());
            //System.out.println("new fluidstack!");
        }else{
            if(this.fluidTank.getFluidAmount() + 1 < this.fluidTank.getCapacity()) {
                //ic2EnergySink.useEnergy(EUperUU);
                this.fill((EnumFacing)null, FluidRegistry.getFluidStack("ic2uu_matter",1).copy(), true);
                //System.out.println(fluidTank.getFluidAmount());
            }
        }
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
}
