package com.yuyuyzl.Accelerator.TileEntities;

import com.yuyuyzl.Accelerator.AcceleratorMod;
import com.yuyuyzl.Accelerator.Blocks.BlockAccHull;
import com.yuyuyzl.Accelerator.CommonProxy;
import ic2.api.energy.prefab.BasicSink;
import ic2.api.item.IC2Items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fluids.*;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by user on 2016/5/28.
 */
public class TEAccMain extends TileEntity implements ITickable,IInventory,IFluidHandler{
    protected FluidTank fluidTank=new FluidTank(1000*8);
    private BasicSink ic2EnergySink = new BasicSink(this,Integer.MAX_VALUE,5);
    public static final int SLOTS_COUNT=1;
    public int dir=0;
    private ItemStack[] itemStacks = new ItemStack[SLOTS_COUNT];
    public boolean isScanning=false;
    public boolean isOn=false;
    public int notifyRefresh=0;
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

    @Override
    public int getSizeInventory() {
        return SLOTS_COUNT;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return itemStacks[index];
    }

    @Override
    public ItemStack decrStackSize(int slotIndex, int count) {
        ItemStack itemStackInSlot = getStackInSlot(slotIndex);
        if (itemStackInSlot == null) return null;

        ItemStack itemStackRemoved;
        if (itemStackInSlot.stackSize <= count) {
            itemStackRemoved = itemStackInSlot;
            setInventorySlotContents(slotIndex, null);
        } else {
            itemStackRemoved = itemStackInSlot.splitStack(count);
            if (itemStackInSlot.stackSize == 0) {
                setInventorySlotContents(slotIndex, null);
            }
        }
        markDirty();
        return itemStackRemoved;
    }

    @Override
    public ItemStack removeStackFromSlot(int slotIndex) {
        ItemStack itemStack = getStackInSlot(slotIndex);
        if (itemStack != null) setInventorySlotContents(slotIndex, null);
        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
        itemStacks[slotIndex] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (this.worldObj.getTileEntity(this.pos) != this) return false;
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    public int EU_Stored,UU_Stored;
    @Override
    public int getField(int id) {
        //System.out.println(id);

        //if(id==0)return EU_Stored%100;
        //if(id==1)return EU_Stored/100%100;
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        //if (id==0)EU_Stored=Math.round(EU_Stored/100)*100+value;
        //if (id==1)EU_Stored=EU_Stored%100+value*100;
    }

    @Override
    public int getFieldCount() {
        return 2;
    }

    @Override
    public void clear() {
        Arrays.fill(itemStacks, null);
    }
    private int time= new Random().nextInt(5);
    private static final double EUperUU=100000;
    private BlockPos posScanning=null;
    private int dirScanning=-1;
    private final Vec3i[] dirVec={new Vec3i(0,0,1),new Vec3i(0,0,-1),new Vec3i(1,0,0),new Vec3i(-1,0,0)};
    @Override
    public void update() {
        ic2EnergySink.update();
        if (ic2EnergySink.canUseEnergy(EUperUU)){
            ItemStack item=getStackInSlot(0);
            /*
            if (item==null){
                ic2EnergySink.useEnergy(EUperUU);
                setInventorySlotContents(0,IC2Items.getItem("misc_resource","matter").copy());
            }else{
                if (item.getItem()==IC2Items.getItem("misc_resource","matter").getItem() && item.stackSize<item.getMaxStackSize()){
                    ic2EnergySink.useEnergy(EUperUU);
                    item.stackSize++;
                }
            }*/
            if (fluidTank.getFluid()==null){
                ic2EnergySink.useEnergy(EUperUU);
                fluidTank.setFluid(FluidRegistry.getFluidStack("ic2uu_matter",1).copy());
                //System.out.println("new fluidstack!");
            }else{
                if(this.fluidTank.getFluidAmount() + 1 < this.fluidTank.getCapacity()) {
                    ic2EnergySink.useEnergy(EUperUU);
                    this.fill((EnumFacing)null, FluidRegistry.getFluidStack("ic2uu_matter",1).copy(), true);
                    //System.out.println(fluidTank.getFluidAmount());
                }
            }
        }
        if(!worldObj.isRemote) {
            EU_Stored = (int) Math.round(ic2EnergySink.getEnergyStored());
            UU_Stored = fluidTank.getFluidAmount();
        }else {
            //System.out.println(EU_Stored);
        }
        if (isScanning && worldObj.getWorldTime()%5==time){
            //do scan

        }
        if (notifyRefresh>0){
            if(worldObj.isRemote)worldObj.markBlockForUpdate(pos);
            this.isScanning=true;
            worldObj.setBlockState(pos.add(dirVec[dir]),AcceleratorMod.proxy.blockAccHull.getStateFromMeta(1));
            //System.out.println(String.valueOf(isOn));
            notifyRefresh--;
        }

        //if(time==0) System.out.println(ic2EnergySink.getEnergyStored());
        //time++;
        //time%=20;
        //if (boomshakalaka)worldObj.createExplosion(null,pos.getX(),pos.getY(),pos.getZ(),10,true);
    }

    @Override
    public String getName() {
        return "container.AccMain.name";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {
        return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
    }

    @Override
    public void writeToNBT(NBTTagCompound parentNBTTagCompound)
    {
        super.writeToNBT(parentNBTTagCompound); // The super call is required to save and load the tiles location

//		// Save the stored item stacks

        // to use an analogy with Java, this code generates an array of hashmaps
        // The itemStack in each slot is converted to an NBTTagCompound, which is effectively a hashmap of key->value pairs such
        //   as slot=1, id=2353, count=1, etc
        // Each of these NBTTagCompound are then inserted into NBTTagList, which is similar to an array.
        NBTTagList dataForAllSlots = new NBTTagList();
        for (int i = 0; i < this.itemStacks.length; ++i) {
            if (this.itemStacks[i] != null) {
                NBTTagCompound dataForThisSlot = new NBTTagCompound();
                dataForThisSlot.setByte("Slot", (byte) i);
                this.itemStacks[i].writeToNBT(dataForThisSlot);
                dataForAllSlots.appendTag(dataForThisSlot);
            }
        }
        // the array of hashmaps is then inserted into the parent hashmap for the container
        parentNBTTagCompound.setTag("Items", dataForAllSlots);

        // Save everything else
        parentNBTTagCompound.setInteger("dir", dir);
        parentNBTTagCompound.setBoolean("on",isOn);
        parentNBTTagCompound.setBoolean("scanning",isScanning);
        //parentNBTTagCompound.setInteger("EU",EU_Stored);
        //parentNBTTagCompound.setBoolean("boom", boomshakalaka);
        //parentNBTTagCompound.setTag("burnTimeRemaining", new NBTTagIntArray(burnTimeRemaining));
        //parentNBTTagCompound.setTag("burnTimeInitial", new NBTTagIntArray(burnTimeInitialValue));
        ic2EnergySink.writeToNBT(parentNBTTagCompound);
        NBTTagCompound fluidTankTag = new NBTTagCompound();
        this.fluidTank.writeToNBT(fluidTankTag);
        parentNBTTagCompound.setTag("fluidTank", fluidTankTag);
    }

    // This is where you load the data that you saved in writeToNBT
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound); // The super call is required to save and load the tiles location
        final byte NBT_TYPE_COMPOUND = 10;       // See NBTBase.createNewByType() for a listing
        NBTTagList dataForAllSlots = nbtTagCompound.getTagList("Items", NBT_TYPE_COMPOUND);

        Arrays.fill(itemStacks, null);           // set all slots to empty
        for (int i = 0; i < dataForAllSlots.tagCount(); ++i) {
            NBTTagCompound dataForOneSlot = dataForAllSlots.getCompoundTagAt(i);
            byte slotNumber = dataForOneSlot.getByte("Slot");
            if (slotNumber >= 0 && slotNumber < this.itemStacks.length) {
                this.itemStacks[slotNumber] = ItemStack.loadItemStackFromNBT(dataForOneSlot);
            }
        }

        // Load everything else.  Trim the arrays (or pad with 0) to make sure they have the correct number of elements
        dir=nbtTagCompound.getInteger("dir");
        isScanning=nbtTagCompound.getBoolean("scanning");
        isOn=nbtTagCompound.getBoolean("on");

        ic2EnergySink.readFromNBT(nbtTagCompound);
        super.readFromNBT(nbtTagCompound);
        this.fluidTank.readFromNBT(nbtTagCompound.getCompoundTag("fluidTank"));
        //EU_Stored=nbtTagCompound.getInteger("EU");
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


    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        return this.canFill(from, resource.getFluid())?fluidTank.fill(resource, doFill):0;
    }
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        return resource != null && resource.isFluidEqual(fluidTank.getFluid())?(!this.canDrain(from, resource.getFluid())?null:fluidTank.drain(resource.amount, doDrain)):null;
    }

    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return !this.canDrain(from, (Fluid)null)?null:fluidTank.drain(maxDrain, doDrain);
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

    public void startScan(){
        if(!this.isOn){
            this.isOn=true;
            this.isScanning=true;
            this.notifyRefresh=1;
        }



    }



}
