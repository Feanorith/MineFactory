package org.jelledevos.minefactory.container;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.util.Iterator;

import org.jelledevos.minefactory.tileentity.TileEntityAccumulator;
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

public class ContainerAccumulator extends Container {

    private TileEntityAccumulator accumulator;
    private int lastCurrentStorage = 0;
    private int lastBurnTime = 0;
    private int lastItemBurnTime = 0;

    public ContainerAccumulator(InventoryPlayer inventoryPlayer, TileEntityAccumulator tileEntityAccumulator) {
        accumulator = tileEntityAccumulator;
        
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
        iCrafting.updateCraftingInventoryInfo(this, 0, accumulator.accumulatorCurrentStorage);
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged.
     */
    public void updateCraftingResults() {
        super.updateCraftingResults();
        Iterator i = this.crafters.iterator();

        while (i.hasNext()) {
            ICrafting iCrafting = (ICrafting)i.next();

            if (lastCurrentStorage != this.accumulator.accumulatorCurrentStorage)
                iCrafting.updateCraftingInventoryInfo(this, 0, accumulator.accumulatorCurrentStorage);
        }

        lastCurrentStorage = accumulator.accumulatorCurrentStorage;
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int barToUpdate, int newValue) {
        if (barToUpdate == 0)
            accumulator.accumulatorCurrentStorage = newValue;
    }

    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return this.accumulator.isUseableByPlayer(entityPlayer);
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift clicking.
     */
    public ItemStack transferStackInSlot(int slotNumber) {
        return null;
    }
}
