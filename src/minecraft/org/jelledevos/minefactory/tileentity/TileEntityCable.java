package org.jelledevos.minefactory.tileentity;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemHoe;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemSword;
import net.minecraft.src.ItemTool;
import net.minecraft.src.Material;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

import org.jelledevos.minefactory.block.powerstation.BlockFossilFuelPS;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntityCable extends TileEntity {

		/** The maximum amount of energy this accumulator can store. */
		public int accumulatorMaxStorage = 1000;
		
		/** The current amount of energy stored in this accumulator. */
		public int accumulatorCurrentStorage = 0;

	    /**
	     * 	Reads a tile entity from NBT.
	     * 	@param tagCompound = The NBTTagCompound to read variables from.
	     */
	    public void readFromNBT(NBTTagCompound tagCompound) {
	        super.readFromNBT(tagCompound);
	        
	        accumulatorCurrentStorage = tagCompound.getShort("CurrentStorage");
	    }

	    /**
	     * 	Writes a tile entity to NBT.
	     * 	@param tagCompound = The NBTTagCompound to write the variables to.
	     */
	    public void writeToNBT(NBTTagCompound tagCompound) {
	        super.writeToNBT(tagCompound);
	        
	        tagCompound.setShort("CurrentStorage", (short)accumulatorCurrentStorage);
	    }


	    /**
	     *	Returns an integer between 0 and the passed value representing how close the current item is to being completely burned.
	     *	@param value = The value to compare the progress to.
	     */
	    @SideOnly(Side.CLIENT)
	    public int getCurrentStorageScaled(int value) {
	    	return accumulatorCurrentStorage * value / 200;
	    }
	    
	    /** Returns the percentage of storage that's taken in this accumulator * 100. E.g. 25% or quarter-full = 25. */
	    @SideOnly(Side.CLIENT)
	    public int getCurrentStoragePercentage() {
	    	return ( accumulatorCurrentStorage * 100 ) / accumulatorMaxStorage; 
	    }
	    
	    @SideOnly(Side.CLIENT) //42
	    public int getCurrentStoragePercentageScaledTo(int pixelValue) {
	    	System.out.println("Storage Percentage: " + getCurrentStoragePercentage());
	    	float i = getCurrentStoragePercentage() / 100f;
	    	System.out.println("Storage Percentage / 100:" + i);
	    	System.out.println("ScaledPercentage:" + pixelValue * i);
	    	return (int) (pixelValue * i);
	    }

	    /**	Updates the entity's state. */
	    public void updateEntity() {
	    	
	    	//Testing function
	    	if(accumulatorCurrentStorage < accumulatorMaxStorage)
	    		accumulatorCurrentStorage = accumulatorMaxStorage;
	    	
//	        boolean isBurning = this.powerStationBurnTime > 0;
//	        boolean inventoryChanged = false;
	//
//	        if (this.powerStationBurnTime > 0) {
//	            --this.powerStationBurnTime;
//	            System.out.println("powerStationBurnTime: " + powerStationBurnTime);
//	        }
	//
//	        if (!this.worldObj.isRemote) {
//	        	if(this.powerStationBurnTime == 0 && canBurnFuel()) {
//	        		System.out.println("ItemBurnTime: " + getItemBurnTime(this.inventoryItemStacks[0]));
//	        		this.itemBurnTickCapacity = this.powerStationBurnTime = getItemBurnTime(this.inventoryItemStacks[0]);
//	        		decrStackSize(0, 1);
//	        		inventoryChanged = true;
//	        	}
//	        	if(inventoryItemStacks[0] == null) {
//	        		for(int i = 1; i < 6; i++) {
//	        			if(inventoryItemStacks[i] != null) {
//	        				inventoryItemStacks[0] = inventoryItemStacks[i];
//	        				inventoryItemStacks[i] = null;
//	        				return;
//	        			}
//	        		}
//	        		
//	        	}
//	        	
//	        	
//	            if (this.powerStationBurnTime == 0 && this.canSmelt()) {
//	                this.itemBurnTickCapacity = this.powerStationBurnTime = getItemBurnTime(this.inventoryItemStacks[1]);
	//
//	                if (this.powerStationBurnTime > 0) {
//	                    inventoryChanged = true;
	//
//	                    if (this.inventoryItemStacks[1] != null) {
//	                        --this.inventoryItemStacks[1].stackSize;
	//
//	                        if (this.inventoryItemStacks[1].stackSize == 0) {
//	                            this.inventoryItemStacks[1] = this.inventoryItemStacks[1].getItem().getContainerItemStack(inventoryItemStacks[1]);
//	                        }
//	                    }
//	                }
//	            }
	//
//	            if (this.isBurning() && this.canSmelt()) {
//	                ++this.itemBurnTickQuantity;
	//
//	                if (this.itemBurnTickQuantity == 200) {
//	                    this.itemBurnTickQuantity = 0;
//	                    this.smeltItem();
//	                    inventoryChanged = true;
//	                }
//	            } else {
//	                this.itemBurnTickQuantity = 0;
//	            }
	//
//	            if (isBurning != this.powerStationBurnTime > 0) {
//	                inventoryChanged = true;
//	                BlockFossilFuelPS.updatePSBlockState(this.powerStationBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
//	            }
//	        }
	//
//	        if (inventoryChanged) {
//	            this.onInventoryChanged();
//	        }
	    }


	    /**
	     * 	Checks whether this tileentity is useable by the player.
	     * 	@param entityPlayer = The player.
	     */
	    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
	        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : entityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	    }
}
