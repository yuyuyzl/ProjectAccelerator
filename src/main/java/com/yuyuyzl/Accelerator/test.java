package com.yuyuyzl.Accelerator;

import com.yuyuyzl.Accelerator.Blocks.BlockAccHull;
import net.minecraft.block.Block;

/**
 * Created by user on 2016/7/7.
 */
public class test {
    public static void main(String[] args) {
        Block a=new BlockAccHull();

        System.out.println(a.getStateFromMeta(1).equals(a.getStateFromMeta(1)));
    }
}
