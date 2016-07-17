package com.yuyuyzl.Accelerator;

import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/7/7.
 */
public class planner {
    //final static double r=20.5;
    static int[][] a=new int[100][100];
    static List<BlockPos> posList=new ArrayList<BlockPos>();
    public static void add(BlockPos p){
        if (!posList.contains(p)){
            posList.add(p);
        }
    }
    public static void main(String[] args) {
        for(double r=1.5;r<=32.5;r+=1) {
            posList.clear();
            a=new int[100][100];
            for (double i = 0; i <= Math.PI / 2; i += 0.000001) {
                a[(int) Math.round(Math.cos(i) * r)][(int) Math.round(Math.sin(i) * r)] = 1;
            }
            for (int i = 0; i <= r; i++) {
                int s = 0;
                for (int j = 0; j <= r; j++) {
                    //System.out.print(a[i][j]);
                    s += a[i][j];
                    if (a[i][j] == 1) {
                        add(new BlockPos(i, 0, j));
                        add(new BlockPos(i, 0, -j));
                        add(new BlockPos(-i, 0, -j));
                        add(new BlockPos(-i, 0, j));
                    }

                }
                //System.out.println(s);
            }
            System.out.println(calculateDrag(posList));
            //System.out.println(posList.size());
        }
    }
    private static double calculateDrag(List<BlockPos> posList){
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
        //System.out.println(String.valueOf(deltaDis*1000/avgDis/posList.size()/posList.size()));
        return deltaDis*1000/avgDis/posList.size()/posList.size();
    }
}
