package com.yuyuyzl.Accelerator.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by user on 2016/7/4.
 */
public class BlockAccHull extends Block{
    public BlockAccHull(){
        super(Material.IRON);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setHardness(0.8F);
    }
    public static final PropertyInteger ON = PropertyInteger.create("on", 0, 1);
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {ON});
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ON,meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ON);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
    }
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.SOLID;
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
