package org.jelledevos.minefactory.container;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Iterator;

import org.jelledevos.minefactory.tileentity.TileEntityFossilFuelPS;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.ICrafting;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.SlotFurnace;
import net.minecraft.src.TileEntityFurnace;

public class ContainerFossilFuelPS extends Container {

    private TileEntityFossilFuelPS powerStation;
    private int lastCookTime = 0;
    private int lastBurnTime = 0;
    private int lastItemBurnTime = 0;

    public ContainerFossilFuelPS(InventoryPlayer inventoryPlayer, TileEntityFossilFuelPS tileEntityFossilFuelPS) {
        this.powerStation = tileEntityFossilFuelPS;
        
        this.addSlotToContainer(new Slot(tileEntityFossilFuelPS, 0, 44, 17));
        
        for(int i = 0; i < 5; i++) {
        	this.addSlotToContainer(new Slot(tileEntityFossilFuelPS, i+1, 8 + (i*18), 53));
        }
        
        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    public void addCraftingToCrafters(ICrafting iCrafting) {
        super.addCraftingToCrafters(iCrafting);
        iCrafting.updateCraftingInventoryInfo(this, 0, this.powerStation.itemBurnTickQuantity);
        iCrafting.updateCraftingInventoryInfo(this, 1, this.powerStation.powerStationBurnTime);
        iCrafting.updateCraftingInventoryInfo(this, 2, this.powerStation.itemBurnTickCapacity);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged.
     */
    public void updateCraftingResults() {
        super.updateCraftingResults();
        Iterator i = this.crafters.iterator();

        while (i.hasNext()) {
            ICrafting iCrafting = (ICrafting)i.next();

            if (this.lastCookTime != this.powerStation.itemBurnTickQuantity)
                iCrafting.updateCraftingInventoryInfo(this, 0, this.powerStation.itemBurnTickQuantity);

            if (this.lastBurnTime != this.powerStation.powerStationBurnTime)
                iCrafting.updateCraftingInventoryInfo(this, 1, this.powerStation.powerStationBurnTime);

            if (this.lastItemBurnTime != this.powerStation.itemBurnTickCapacity)
                iCrafting.updateCraftingInventoryInfo(this, 2, this.powerStation.itemBurnTickCapacity);
        }

        this.lastCookTime = this.powerStation.itemBurnTickQuantity;
        this.lastBurnTime = this.powerStation.powerStationBurnTime;
        this.lastItemBurnTime = this.powerStation.itemBurnTickCapacity;
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int barToUpdate, int newValue) {
        if (barToUpdate == 0)
            this.powerStation.itemBurnTickQuantity = newValue;

        if (barToUpdate == 1)
            this.powerStation.powerStationBurnTime = newValue;

        if (barToUpdate == 2)
            this.powerStation.itemBurnTickCapacity = newValue;
    }

    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return this.powerStation.isUseableByPlayer(entityPlayer);
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift clicking.
     */
    public ItemStack transferStackInSlot(int slotNumber) {
        ItemStack itemStack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotNumber);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotItemStack = slot.getStack();
            itemStack = slotItemStack.copy();

            if (slotNumber == 2) {
                if (!this.mergeItemStack(slotItemStack, 3, 39, true))
                    return null;

                slot.onSlotChange(slotItemStack, itemStack);
            } else if (slotNumber != 1 && slotNumber != 0) {
                if (FurnaceRecipes.smelting().getSmeltingResult(slotItemStack) != null) {
                    if (!this.mergeItemStack(slotItemStack, 0, 1, false)) {
                        return null;
                    }
                } else if (TileEntityFurnace.isItemFuel(slotItemStack)) {
                    if (!this.mergeItemStack(slotItemStack, 1, 2, false))
                        return null;
                } else if (slotNumber >= 3 && slotNumber < 30) {
                    if (!this.mergeItemStack(slotItemStack, 30, 39, false))
                        return null;
                } else if (slotNumber >= 30 && slotNumber < 39 && !this.mergeItemStack(slotItemStack, 3, 30, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(slotItemStack, 3, 39, false)) {
                return null;
            }

            if (slotItemStack.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }

            if (slotItemStack.stackSize == itemStack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(slotItemStack);
        }

        return itemStack;
    }
}
