package com.yuyuyzl.Accelerator.Guis;

import com.yuyuyzl.Accelerator.AcceleratorMod;
import com.yuyuyzl.Accelerator.Containers.ContainerAccMain;
import com.yuyuyzl.Accelerator.Network.MessageExplode;
import com.yuyuyzl.Accelerator.Network.MessageScan;
import com.yuyuyzl.Accelerator.Network.NetworkHandler;
import com.yuyuyzl.Accelerator.TileEntities.TEAccMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

/**
 * Created by user on 2016/5/29.
 */
public class GuiAccMain extends GuiContainer{
    private TEAccMain te;
    public GuiAccMain(InventoryPlayer invPlayer, TEAccMain te) {
        super(new ContainerAccMain(invPlayer,te));
        this.te=te;
        xSize = 176;
        ySize = 207;
    }
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("acceleratormod","textures/GUIAccMainbg.png"));
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        fontRendererObj.drawString("P.A. Controller Ver."+ AcceleratorMod.VERSION,guiLeft+24,guiTop+21, Color.white.getRGB());
        if(te.isOn) {
            if (!te.isScanning) {
                if (te.assemblePercent==-3){
                    fontRendererObj.drawString("Fatal Error Occurred.", guiLeft + 24, guiTop + 31, Color.white.getRGB());

                }else {
                    fontRendererObj.drawString("Progress : " + String.valueOf((int)(te.progressInt/100))+(te.progressInt%100<10?".0":".")+ String.valueOf(te.progressInt%100)+ "%", guiLeft + 24, guiTop + 31, Color.white.getRGB());
                    fontRendererObj.drawString("UU Matter : " + String.valueOf(te.UU_Stored) + " mB", guiLeft + 24, guiTop + 41, Color.white.getRGB());
                }
            }else {
                fontRendererObj.drawString("Scanning.", guiLeft + 24, guiTop + 31, Color.white.getRGB());
                if(te.assemblePercent==0){
                    fontRendererObj.drawString("Detecting route.", guiLeft + 24, guiTop + 41, Color.white.getRGB());
                }else {
                    fontRendererObj.drawString("Assembling. "+String.valueOf(te.assemblePercent)+"% finished.", guiLeft + 24, guiTop + 41, Color.white.getRGB());
                }
            }
        }else {
            fontRendererObj.drawString("Idle.", guiLeft + 24, guiTop + 31, Color.white.getRGB());
            fontRendererObj.drawString("Ready to Scan.", guiLeft + 24, guiTop + 41, Color.white.getRGB());
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (isinRect(mouseX,mouseY,86,97,116,113)){
           drawRect(86,97,116,113,new Color(255,255,255,64).getRGB());
        }
        if (isinRect(mouseX,mouseY,126,97,156,113)){
            drawRect(126,97,156,113,new Color(255,255,255,64).getRGB());
        }

    }


    private boolean isinRect(int mouseX,int mouseY,int left,int top,int right,int down){
        return mouseX>=guiLeft+left && mouseY>=guiTop+top && mouseX<=guiLeft+right && mouseY<=guiTop+down;
    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isinRect(mouseX,mouseY,86,97,116,113) && mouseButton==0){
            //NetworkHandler.INSTANCE.sendToServer(new MessageExplode(te.getPos().getX(),te.getPos().getY(),te.getPos().getZ(),10.0F));
            NetworkHandler.INSTANCE.sendToServer(new MessageScan(te.getPos().getX(),te.getPos().getY(),te.getPos().getZ()));
            System.out.println("button 1 pressed");
        }
        if (isinRect(mouseX,mouseY,126,97,156,113) && mouseButton==0){
            //NetworkHandler.INSTANCE.sendToServer(new MessageExplode(te.getPos().getX(),te.getPos().getY(),te.getPos().getZ(),10.0F));
            System.out.println("button 2 pressed");
        }
    }
}
