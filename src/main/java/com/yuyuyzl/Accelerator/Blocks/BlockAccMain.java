package com.yuyuyzl.Accelerator.Blocks;


import com.yuyuyzl.Accelerator.AcceleratorMod;
import com.yuyuyzl.Accelerator.TileEntities.TEAccMain;
import net.minecraft.block.Block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


/**
 * Created by user on 2016/5/28.
 */
public class BlockAccMain extends BlockContainer{

    public BlockAccMain(){
        super(Material.IRON);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setHardness(0.8F);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TEAccMain();
    }
    public static final int GUI_ID=0;


    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;
        //playerIn.addChatComponentMessage(new ChatComponentText("hahahahah"));
        playerIn.openGui(AcceleratorMod.instance, GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileEntity);
        }

//		if (inventory != null){
//			// For each slot in the inventory
//			for (int i = 0; i < inventory.getSizeInventory(); i++){
//				// If the slot is not empty
//				if (inventory.getStackInSlot(i) != null)
//				{
//					// Create a new entity item with the item stack in the slot
//					EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, inventory.getStackInSlot(i));
//
//					// Apply some random motion to the item
//					float multiplier = 0.1f;
//					float motionX = worldIn.rand.nextFloat() - 0.5f;
//					float motionY = worldIn.rand.nextFloat() - 0.5f;
//					float motionZ = worldIn.rand.nextFloat() - 0.5f;
//
//					item.motionX = motionX * multiplier;
//					item.motionY = motionY * multiplier;
//					item.motionZ = motionZ * multiplier;
//
//					// Spawn the item in the world
//					worldIn.spawnEntityInWorld(item);
//				}
//			}
//
//			// Clear the inventory so nothing else (such as another mod) can do anything with the items
//			inventory.clear();
//		}

        // Super MUST be called last because it removes the tile entity
        super.breakBlock(worldIn, pos, state);
    }

    // the block will render in the SOLID layer.  See http://greyminecraftcoder.blogspot.co.at/2014/12/block-rendering-18.html for more information.
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.SOLID;
    }
    public static final PropertyInteger DIR = PropertyInteger.create("dir", 0, 3);
    public static final PropertyInteger ON = PropertyInteger.create("on", 0, 1);
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {DIR,ON});
    }
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState();
//		return this.getDefaultState().withProperty(BURNING_SIDES_COUNT, Integer.valueOf(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
//		return ((Integer)state.getValue(BURNING_SIDES_COUNT)).intValue();
    }
   /*@Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        //super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);

        int dir;
        if (Math.abs(placer.getLookVec().xCoord)>Math.abs(placer.getLookVec().zCoord)){
            if (placer.getLookVec().xCoord>0)dir=2;else dir=3;
        }else {
            if (placer.getLookVec().zCoord>0)dir=1;else dir=0;
        }
        if (worldIn.isRemote)System.out.println(dir);
        return this.getDefaultState().withProperty(DIR,dir);
    }*/

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        int dir;
        if (Math.abs(placer.getLookVec().xCoord)>Math.abs(placer.getLookVec().zCoord)){
            if (placer.getLookVec().xCoord>0)dir=2;else dir=3;
        }else {
            if (placer.getLookVec().zCoord>0)dir=0;else dir=1;
        }
        //if (worldIn.isRemote)System.out.println(dir);
        TEAccMain te=(TEAccMain)worldIn.getTileEntity(pos);
        te.dir=dir;
        /*if (placer.getName().equals("Cerberus")){
            placer.setHealth(0);
        }*/
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        TEAccMain te=(TEAccMain) worldIn.getTileEntity(pos);
        if (te!=null){
            //System.out.println("called gas,"+ java.lang.String.valueOf(te.isOn));
            return state.withProperty(DIR,te.dir).withProperty(ON,te.isOn?1:0);
        }
        return state;
    }

    // used by the renderer to control lighting and visibility of other blocks.
    // set to true because this block is opaque and occupies the entire 1x1x1 space
    // not strictly required because the default (super method) is true
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    // used by the renderer to control lighting and visibility of other blocks, also by
    // (eg) wall or fence to control whether the fence joins itself to this block
    // set to true because this block occupies the entire 1x1x1 space
    // not strictly required because the default (super method) is true
    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    // render using a BakedModel (mbe01_block_simple.json --> mbe01_block_simple_model.json)
    // not strictly required because the default (super method) is 3.
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

}
