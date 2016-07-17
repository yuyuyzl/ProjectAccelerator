package com.yuyuyzl.Accelerator.TileEntities;

import com.yuyuyzl.Accelerator.AcceleratorMod;

import com.yuyuyzl.Accelerator.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 2016/5/28.
 */
public class TEAccMain extends TileEntity implements ITickable,IInventory{
    //protected FluidTank fluidTank=new FluidTank(1000*8);
    //private BasicSink ic2EnergySink = new BasicSink(this,Integer.MAX_VALUE,5);
    public static final int SLOTS_COUNT=1;
    public int dir=0;
    private ItemStack[] itemStacks = new ItemStack[SLOTS_COUNT];
    public boolean isScanning=false;
    public boolean isOn=false;
    public int notifyRefresh=0;

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

    public int EU_Stored,UU_Stored,progressInt=0;
    public double accProgress=0;

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
    private int time= 0;
    //private static final double EUperUU=100000;
    private BlockPos posScanning=pos;
    private int dirScanning=dir;
    private static final Vec3i[] dirVec={new Vec3i(0,0,1),new Vec3i(0,0,-1),new Vec3i(1,0,0),new Vec3i(-1,0,0)};
    private static final int[] dirRight={3,2,0,1};
    private static final int[] dirLeft={2,3,1,0};
    private List<BlockPos> hullsPos=new ArrayList<BlockPos>();
    private List<BlockPos> energyPos=new ArrayList<BlockPos>();
    private List<BlockPos> fluidPos=new ArrayList<BlockPos>();
    private List<BlockPos> routePos=new ArrayList<BlockPos>();
    private double drag =0;

    public int assemblePercent=-2;
    private int hullChangei=0;
    @Override
    public void update() {
        if(assemblePercent==-1 && !worldObj.isRemote){
            if (worldObj.getWorldTime()%2==time){
                if (hullChangei == hullsPos.size()) {
                    hullChangei = 0;
                }
                if (worldObj.getBlockState(hullsPos.get(hullChangei * 97 % hullsPos.size())) != AcceleratorMod.proxy.blockAccHull.getStateFromMeta(1)) {
                    isOn = true;
                    isScanning = false;
                    if (worldObj.isRemote) worldObj.markBlockForUpdate(pos);
                    assemblePercent = -3;
                    return;
                }
                hullChangei++;
            }
            for (BlockPos p:energyPos){
                if(worldObj.getTileEntity(p)!=null && worldObj.getTileEntity(p) instanceof TEAccEnergy) {
                    TEAccEnergy te = (TEAccEnergy) worldObj.getTileEntity(p);
                    EU_Stored += te.getEnergy(Integer.MAX_VALUE - EU_Stored);
                }else {
                    isOn=true;
                    isScanning=false;
                    if(worldObj.isRemote)worldObj.markBlockForUpdate(pos);
                    assemblePercent=-3;
                    return;
                }
            }
            accProgress+=calculateAcceleration(drag,EU_Stored,Config.kAcceleration,Config.kOverall);
            if (accProgress<0)accProgress=0;
            EU_Stored=0;
            if (accProgress>=100){
                accProgress-=100;
                UU_Stored++;
            }
            progressInt=(int)Math.round(accProgress*100);

        }
        //System.out.println(String.valueOf(isOn)+String.valueOf(isScanning)+String.valueOf(assemblePercent));
        /*
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
            }
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
        }*/
        if (isScanning && worldObj.getWorldTime()%2==time){
            //do scan
               if (!worldObj.isRemote) doScan();
        }
        if (notifyRefresh>0){
            if(worldObj.isRemote)worldObj.markBlockForUpdate(pos);
            assemblePercent=0;
            posScanning=pos.add(dirVec[dir]).add(dirVec[dir]);
            dirScanning=dir;
            hullsPos.clear();
            energyPos.clear();
            routePos.clear();
            hullChangei=0;

            this.isScanning=true;

            //worldObj.setBlockState(pos.add(dirVec[dir]),AcceleratorMod.proxy.blockAccHull.getStateFromMeta(1));
            //System.out.println(String.valueOf(isOn));
            notifyRefresh--;
        }

        //if(time==0) System.out.println(ic2EnergySink.getEnergyStored());
        //time++;
        //time%=20;
        //if (boomshakalaka)worldObj.createExplosion(null,pos.getX(),pos.getY(),pos.getZ(),10,true);
    }
    private void addtolist(BlockPos p){
        if (worldObj.getTileEntity(p)==null&&!hullsPos.contains(p))hullsPos.add(p);
        if (worldObj.getTileEntity(p) instanceof TEAccEnergy&&!energyPos.contains(p)){
            energyPos.add(p);
            System.out.println("added energy at "+p.toString());
        }
        if (worldObj.getTileEntity(p) instanceof TEAccFluid&&!fluidPos.contains(p)){
            fluidPos.add(p);
            System.out.println("added fluid port at "+p.toString());
        }
    }

    private boolean isHull(BlockPos p,boolean canBeMachine){
        return worldObj.getBlockState(p)==AcceleratorMod.proxy.blockAccHull.getDefaultState()||
                //worldObj.getBlockState(p)==AcceleratorMod.proxy.blockAccHull.getStateFromMeta(1)||
                (canBeMachine&&p.equals(pos))||worldObj.getTileEntity(p) instanceof TEAccFluid||
                (canBeMachine&&p.equals(pos))||worldObj.getTileEntity(p) instanceof TEAccEnergy;
    }
    private void doScan(){
        if(hullsPos.isEmpty()||!posScanning.equals(pos.add(dirVec[dir]).add(dirVec[dir]))){
            /*boolean isLeftHull=worldObj.getBlockState(posScanning.add(dirVec[dirLeft[dirScanning]]))==AcceleratorMod.proxy.blockAccHull.getDefaultState()||
                    posScanning.add(dirVec[dirLeft[dirScanning]]).equals(pos);
            isLeftHull=isLeftHull&&worldObj.getBlockState(posScanning.add(dirVec[dirLeft[dirScanning]]).up())==AcceleratorMod.proxy.blockAccHull.getDefaultState()&&
                    worldObj.getBlockState(posScanning.add(dirVec[dirLeft[dirScanning]]).down())==AcceleratorMod.proxy.blockAccHull.getDefaultState();
            boolean isRightHull=worldObj.getBlockState(posScanning.add(dirVec[dirRight[dirScanning]]))==AcceleratorMod.proxy.blockAccHull.getDefaultState()||
                    posScanning.add(dirVec[dirRight[dirScanning]]).equals(pos);
            isRightHull=isRightHull&&worldObj.getBlockState(posScanning.add(dirVec[dirRight[dirScanning]]).up())==AcceleratorMod.proxy.blockAccHull.getDefaultState()&&
                    worldObj.getBlockState(posScanning.add(dirVec[dirRight[dirScanning]]).down())==AcceleratorMod.proxy.blockAccHull.getDefaultState();
            boolean isFrontHull=worldObj.getBlockState(posScanning.add(dirVec[dirScanning]))==AcceleratorMod.proxy.blockAccHull.getDefaultState()&&
                    worldObj.getBlockState(posScanning.add(dirVec[dirScanning]).up())==AcceleratorMod.proxy.blockAccHull.getDefaultState()&&
                    worldObj.getBlockState(posScanning.add(dirVec[dirScanning]).down())==AcceleratorMod.proxy.blockAccHull.getDefaultState();*/
            boolean isLeftHull=isHull(posScanning.add(dirVec[dirLeft[dirScanning]]),true)
                    //&&isHull(posScanning.add(dirVec[dirLeft[dirScanning]]).down(),true)
                    //&&isHull(posScanning.add(dirVec[dirLeft[dirScanning]]).up(),true)
                    ;
            boolean isRightHull=isHull(posScanning.add(dirVec[dirRight[dirScanning]]),true)
                    //&&isHull(posScanning.add(dirVec[dirRight[dirScanning]]).down(),true)
                    //&&isHull(posScanning.add(dirVec[dirRight[dirScanning]]).up(),true)
                    ;
            boolean isFrontHull=isHull(posScanning.add(dirVec[dirScanning]),true)
                    //&&isHull(posScanning.add(dirVec[dirScanning]).up(),true)
                    //&&isHull(posScanning.add(dirVec[dirScanning]).down(),true)
                    ;
            if(isHull(posScanning.up(),false)&&
                    isHull(posScanning.down(),false)){
                //worldObj.setBlockState(posScanning,AcceleratorMod.proxy.blockAccHull.getStateFromMeta(1));
                if(isLeftHull&&isRightHull&&!isFrontHull){
                    addtolist(posScanning.up());
                    addtolist(posScanning.down());
                    addtolist(posScanning.add(dirVec[dirLeft[dirScanning]]));
                    addtolist(posScanning.add(dirVec[dirRight[dirScanning]]));
                    //addtolist(posScanning.add(dirVec[dirLeft[dirScanning]]).up());
                    //addtolist(posScanning.add(dirVec[dirRight[dirScanning]]).up());
                    //addtolist(posScanning.add(dirVec[dirLeft[dirScanning]]).down());
                    //addtolist(posScanning.add(dirVec[dirRight[dirScanning]]).down());
                    routePos.add(posScanning);
                    posScanning=posScanning.add(dirVec[dirScanning]);
                    return;
                }


                if(isLeftHull&&isFrontHull&&worldObj.isAirBlock(posScanning.add(dirVec[dirRight[dirScanning]]))){
                    addtolist(posScanning.up());
                    addtolist(posScanning.down());
                    addtolist(posScanning.add(dirVec[dirLeft[dirScanning]]));
                    addtolist(posScanning.add(dirVec[dirScanning]));
                    //addtolist(posScanning.add(dirVec[dirScanning]).add(dirVec[dirLeft[dirScanning]]));
                    //addtolist(posScanning.add(dirVec[dirLeft[dirScanning]]).up());
                    //addtolist(posScanning.add(dirVec[dirScanning]).up());
                    //addtolist(posScanning.add(dirVec[dirScanning]).add(dirVec[dirLeft[dirScanning]]).up());
                    //addtolist(posScanning.add(dirVec[dirLeft[dirScanning]]).down());
                    //addtolist(posScanning.add(dirVec[dirScanning]).down());
                    //addtolist(posScanning.add(dirVec[dirScanning]).add(dirVec[dirLeft[dirScanning]]).down());
                    dirScanning=dirRight[dirScanning];
                    //System.out.println("turning right");
                    routePos.add(posScanning);
                    posScanning=posScanning.add(dirVec[dirScanning]);
                    return;
                }
                if(isRightHull&&isFrontHull&&worldObj.isAirBlock(posScanning.add(dirVec[dirLeft[dirScanning]]))){
                    addtolist(posScanning.up());
                    addtolist(posScanning.down());
                    addtolist(posScanning.add(dirVec[dirScanning]));
                    addtolist(posScanning.add(dirVec[dirRight[dirScanning]]));
                    //addtolist(posScanning.add(dirVec[dirScanning]).add(dirVec[dirRight[dirScanning]]));
                    //addtolist(posScanning.add(dirVec[dirScanning]).up());
                    //addtolist(posScanning.add(dirVec[dirRight[dirScanning]]).up());
                    //addtolist(posScanning.add(dirVec[dirScanning]).add(dirVec[dirRight[dirScanning]]).up());
                    //addtolist(posScanning.add(dirVec[dirScanning]).down());
                    //addtolist(posScanning.add(dirVec[dirRight[dirScanning]]).down());
                    //addtolist(posScanning.add(dirVec[dirScanning]).add(dirVec[dirRight[dirScanning]]).down());
                    dirScanning=dirLeft[dirScanning];
                    //System.out.println("turning left");
                    routePos.add(posScanning);
                    posScanning=posScanning.add(dirVec[dirScanning]);
                    return;
                }
            }
        }else {
        //System.out.println("scan finished");
        //System.out.println("result:"+routePos.toString());
        if (posScanning.equals(pos.add(dirVec[dir]).add(dirVec[dir]))&& hullsPos.size()>0){
            if(hullChangei==0) {
                for (BlockPos p:energyPos) {
                    TEAccEnergy te= (TEAccEnergy) worldObj.getTileEntity(p);
                    te.isOn=true;
                    worldObj.markBlockForUpdate(p);
                }
                drag = calculateDrag(routePos);
            }
            if(worldObj.getBlockState(hullsPos.get(hullChangei*97%hullsPos.size()))==AcceleratorMod.proxy.blockAccHull.getDefaultState()) {
                worldObj.setBlockState(hullsPos.get(hullChangei * 97 % hullsPos.size()), AcceleratorMod.proxy.blockAccHull.getStateFromMeta(1));
                assemblePercent=Math.round(hullChangei*100/hullsPos.size());
            }else {
                isOn=true;
                isScanning=false;
                if(worldObj.isRemote)worldObj.markBlockForUpdate(pos);
                assemblePercent=-3;
                return;
            }

            hullChangei++;
            if (hullChangei==hullsPos.size()){
                isScanning=false;
                assemblePercent=-1;
                System.out.println("finished");
            }
        }else{
            System.out.println("-2.ison=false,line327");
            System.out.println(String.valueOf(isScanning));
            isOn=false;
            isScanning=false;
            if(worldObj.isRemote)worldObj.markBlockForUpdate(pos);
            assemblePercent=-2;
        }
        }
    }
    private double calculateDrag(List<BlockPos> posList){
        double avgX=0,avgY=0,avgZ=0,avgDis=0,deltaDis=0;
        for(BlockPos p:posList){
            avgX+=p.getX();
            avgY+=p.getY();
            avgZ+=p.getZ();
        }
        avgX/=posList.size();
        avgY/=posList.size();
        avgZ/=posList.size();
        for(BlockPos p:posList)avgDis+=Math.sqrt(p.distanceSq(avgX,avgY,avgZ));
        avgDis/=posList.size();
        //for(BlockPos p:posList)deltaDis+=Math.pow((p.distanceSq(avgX,avgY,avgZ)-avgDis-1)<0?0:(p.distanceSq(avgX,avgY,avgZ)-avgDis-1),2);
        for(BlockPos p:posList){
            double d=Math.pow((Math.sqrt(p.distanceSq(avgX,avgY,avgZ))-avgDis),2);
            //System.out.println(String.valueOf(d));
            //d=(d-0.5<0)?0:d-0.5;
            deltaDis+=d;
        }
        deltaDis=Math.sqrt(deltaDis);
        System.out.println(String.valueOf(deltaDis*1000/avgDis/posList.size()/posList.size()));
        //System.out.println(Config.kOverall);
        return deltaDis*1000/avgDis/posList.size()/posList.size();
    }
    private double calculateAcceleration(double drag,double eu,double kAcceleration,double kOverall){
        return kOverall*(kAcceleration*Math.sqrt(eu)-drag);
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
    private int[] posListtoArr(List<BlockPos> posList){
        int[] arr=new int[posList.size()*3];
        for(int i=0;i<posList.size();i++){
            arr[i*3]=posList.get(i).getX();
            arr[i*3+1]=posList.get(i).getY();
            arr[i*3+2]=posList.get(i).getZ();
        }
        return arr;
    }
    private List<BlockPos> arrtoPosList(int[] arr){
        List<BlockPos> posList=new ArrayList<BlockPos>();
        for(int i=0;i<arr.length/3;i++)posList.add(new BlockPos(arr[i*3],arr[i*3+1],arr[i*3+2]));
        return posList;
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
        parentNBTTagCompound.setInteger("posscanningx",posScanning.getX());
        parentNBTTagCompound.setInteger("posscanningy",posScanning.getY());
        parentNBTTagCompound.setInteger("posscanningz",posScanning.getZ());
        parentNBTTagCompound.setInteger("dirscanning",dirScanning);
        parentNBTTagCompound.setInteger("hullchangei",hullChangei);
        parentNBTTagCompound.setInteger("assemblepercent",assemblePercent);
        parentNBTTagCompound.setIntArray("hullspos",posListtoArr(hullsPos));
        parentNBTTagCompound.setIntArray("energypos",posListtoArr(energyPos));
        parentNBTTagCompound.setIntArray("fluidpos",posListtoArr(fluidPos));
        parentNBTTagCompound.setInteger("EU",EU_Stored);
        parentNBTTagCompound.setInteger("UU",UU_Stored);
        parentNBTTagCompound.setDouble("drag", drag);
        parentNBTTagCompound.setDouble("accprogress",accProgress);
        //parentNBTTagCompound.setBoolean("boom", boomshakalaka);
        //parentNBTTagCompound.setTag("burnTimeRemaining", new NBTTagIntArray(burnTimeRemaining));
        //parentNBTTagCompound.setTag("burnTimeInitial", new NBTTagIntArray(burnTimeInitialValue));
        //ic2EnergySink.writeToNBT(parentNBTTagCompound);
        //NBTTagCompound fluidTankTag = new NBTTagCompound();
        //this.fluidTank.writeToNBT(fluidTankTag);
        //parentNBTTagCompound.setTag("fluidTank", fluidTankTag);
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
        dirScanning=nbtTagCompound.getInteger("dirscanning");
        posScanning=new BlockPos(nbtTagCompound.getInteger("posscanningx"),nbtTagCompound.getInteger("posscanningy"),nbtTagCompound.getInteger("posscanningz"));
        hullChangei=nbtTagCompound.getInteger("hullchangei");
        assemblePercent=nbtTagCompound.getInteger("assemblepercent");
        hullsPos=arrtoPosList(nbtTagCompound.getIntArray("hullspos"));
        energyPos=arrtoPosList(nbtTagCompound.getIntArray("energypos"));
        fluidPos=arrtoPosList(nbtTagCompound.getIntArray("fluidpos"));
        drag =nbtTagCompound.getDouble("drag");
        accProgress=nbtTagCompound.getDouble("accprogress");
        //ic2EnergySink.readFromNBT(nbtTagCompound);
        //super.readFromNBT(nbtTagCompound);
        //this.fluidTank.readFromNBT(nbtTagCompound.getCompoundTag("fluidTank"));
        EU_Stored=nbtTagCompound.getInteger("EU");
        UU_Stored=nbtTagCompound.getInteger("UU");
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

    /*
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
*/
    public void startScan(){
        if(!worldObj.isRemote){
            if(!this.isOn){

                this.isOn=true;
                this.isScanning=true;
                this.hullChangei=0;

                //worldObj.setBlockState(pos.add(dirVec[dirLeft[dir]]),AcceleratorMod.proxy.blockAccHull.getStateFromMeta(1));
                this.notifyRefresh=1;
            }else {
                for(BlockPos p:hullsPos){
                    if(worldObj.getBlockState(p)==AcceleratorMod.proxy.blockAccHull.getStateFromMeta(1))
                        worldObj.setBlockState(p,AcceleratorMod.proxy.blockAccHull.getDefaultState());
                }
                for(BlockPos p:energyPos){
                    if(worldObj.getTileEntity(p) instanceof TEAccEnergy){
                        ((TEAccEnergy)worldObj.getTileEntity(p)).isOn=false;
                    }
                }
                this.isOn=false;
                this.isScanning=false;
                this.assemblePercent=-2;
            }
        }else{
            if(assemblePercent==0) {

                this.isOn = true;
                this.isScanning = true;
                this.hullChangei = 0;
                //worldObj.setBlockState(pos.add(dirVec[dirLeft[dir]]),AcceleratorMod.proxy.blockAccHull.getStateFromMeta(1));
                this.notifyRefresh = 1;
            }else {
                if(assemblePercent==-1){
                    this.isOn=false;
                    this.isScanning=false;
                    this.assemblePercent=-2;
                }
            }
        }



    }



}
