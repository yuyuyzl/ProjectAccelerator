package com.yuyuyzl.Accelerator.Network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by user on 2016/5/30.
 */
public class MessageExplode implements IMessage,IMessageHandler<MessageExplode,IMessage>{
    float x,y,z,strength;
    public MessageExplode(){}
    public MessageExplode(float x , float y, float z, float strength){
        this.x=x;
        this.y=y;
        this.z=z;
        this.strength=strength;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        this.x=buf.readFloat();
        this.y=buf.readFloat();
        this.z=buf.readFloat();
        this.strength=buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
        buf.writeFloat(strength);

    }

    @Override
    public IMessage onMessage(final MessageExplode message, final MessageContext ctx) {
        final IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                ctx.getServerHandler().playerEntity.worldObj.createExplosion(null,message.x,message.y,message.z,message.strength,true);
            }
        });
        return null; // no response in this case
    }
}
