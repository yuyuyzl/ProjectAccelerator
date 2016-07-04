package com.yuyuyzl.Accelerator.Network;

import com.yuyuyzl.Accelerator.TileEntities.TEAccMain;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by user on 2016/7/4.
 */
public class MessageScan implements IMessage,IMessageHandler<MessageScan,IMessage> {
    int x,y,z;
    public MessageScan(){}
    public MessageScan(int x , int y, int z){
        this.x=x;
        this.y=y;
        this.z=z;

    }
    @Override
    public void fromBytes(ByteBuf buf) {
        this.x=buf.readInt();
        this.y=buf.readInt();
        this.z=buf.readInt();

    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);


    }

    @Override
    public IMessage onMessage(final MessageScan message, final MessageContext ctx) {
        final IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                ((TEAccMain)ctx.getServerHandler().playerEntity.worldObj.getTileEntity(new BlockPos(message.x,message.y,message.z))).startScan();
            }
        });
        final IThreadListener mainThreadC = Minecraft.getMinecraft(); // or Minecraft.getMinecraft() on the client
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                ((TEAccMain)Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(new BlockPos(message.x,message.y,message.z))).startScan();
            }
        });
        return null; // no response in this case
    }
}
