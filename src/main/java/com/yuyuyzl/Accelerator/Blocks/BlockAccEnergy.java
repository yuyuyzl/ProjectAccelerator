package com.yuyuyzl.Accelerator.Blocks;

import com.yuyuyzl.Accelerator.TileEntities.TEAccEnergy;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by user on 2016/7/5.
 */
public class BlockAccEnergy extends Block implements ITileEntityProvider{
    public BlockAccEnergy(){
        super(Material.iron);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(0.8F);
    }
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
    }
    public static final PropertyInteger ON = PropertyInteger.create("on", 0, 1);

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {ON});
    }
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.SOLID;
    }

    // used by the renderer to control lighting and visibility of other blocks.
    // set to true because this block is opaque and occupies the entire 1x1x1 space
    // not strictly required because the default (super method) is true
    @Override
    public boolean isOpaqueCube() {
        return true;
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
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        //if(!worldIn.isRemote)playerIn.addChatComponentMessage(new ChatComponentText(String.valueOf(((TEAccEnergy)worldIn.getTileEntity(pos)).getEnergyStored())));
        if(worldIn.isRemote)playerIn.addChatComponentMessage(new ChatComponentText(String.valueOf(((TEAccEnergy)worldIn.getTileEntity(pos)).isOn)));
        return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
    }

    // used by the renderer to control lighting and visibility of other blocks, also by
    // (eg) wall or fence to control whether the fence joins itself to this block
    // set to true because this block occupies the entire 1x1x1 space
    // not strictly required because the default (super method) is true
    @Override
    public boolean isFullCube() {
        return true;
    }

    // render using a BakedModel (mbe01_block_simple.json --> mbe01_block_simple_model.json)
    // not strictly required because the default (super method) is 3.
    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TEAccEnergy();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return getDefaultState().withProperty(ON,((TEAccEnergy)worldIn.getTileEntity(pos)).isOn?1:0);
    }
}
