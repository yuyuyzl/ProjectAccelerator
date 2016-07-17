package com.yuyuyzl.Accelerator.Containers;

import com.yuyuyzl.Accelerator.TileEntities.TEAccMain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by user on 2016/5/29.
 */
public class ContainerAccMain extends Container{
    private TEAccMain te;
    private final int HOTBAR_SLOT_COUNT = 9;
    private final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;


    public ContainerAccMain(InventoryPlayer invPlayer, TEAccMain te){
        this.te=te;
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 183;
        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
            int slotNumber = x;
            addSlotToContainer(new Slot(invPlayer, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
        }

        final int PLAYER_INVENTORY_XPOS = 8;
        final int PLAYER_INVENTORY_YPOS = 125;
        // Add the rest of the players inventory to the gui
        for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new Slot(invPlayer, slotNumber,  xpos, ypos));
            }
        }
        final int TEST_SLOT_XPOS = 60;
        final int TEST_SLOT_YPOS = 90;
        addSlotToContainer(new SlotTest(te,0,TEST_SLOT_XPOS,TEST_SLOT_YPOS));

    }
    public class SlotTest extends Slot{
        public SlotTest(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }
    }
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return te.isUseableByPlayer(playerIn);
    }
    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        if(id==0)te.EU_Stored=data;
        if(id==1)te.UU_Stored=data;
        if(id==2){
            te.assemblePercent=data;
            if (data==-1||data==-3){
                te.isScanning=false;
                te.isOn=true;
            }
            else
            if (data==-2){
                te.isOn=false;
                te.isScanning=false;
            }else {
                te.isOn = true;
                te.isScanning = true;
            }
        }
        if(id==3)te.progressInt=data;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        // go through the list of crafters (players using this container) and update them if necessary
        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icrafting =this.listeners.get(i);
            //ICrafting icrafting = (ICrafting)this.crafters.get(i);
            icrafting.sendProgressBarUpdate(this,0,te.EU_Stored);
            icrafting.sendProgressBarUpdate(this,1,te.UU_Stored);
            icrafting.sendProgressBarUpdate(this,2,te.assemblePercent);
            icrafting.sendProgressBarUpdate(this,3,te.progressInt);

        }

    }
}
