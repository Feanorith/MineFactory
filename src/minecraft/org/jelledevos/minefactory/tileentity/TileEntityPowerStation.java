package org.jelledevos.minefactory.tileentity;

//import static org.jelledevos.minefactory.ElectricConnectionManager.getEnergyDestinations;
import java.util.ArrayList;

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
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

import org.jelledevos.minefactory.ElectricConnectionManager;
import org.jelledevos.minefactory.block.powerstation.BlockFossilFuelPS;
import org.jelledevos.minefactory.interfaces.IElectricalMedium;
import org.jelledevos.minefactory.interfaces.IElectricalUnit;
import org.jelledevos.minefactory.interfaces.IElectricityProducer;
import org.jelledevos.minefactory.interfaces.IElectricityReceiver;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntityPowerStation extends TileEntity implements IInventory, ISidedInventory, IElectricalUnit {
	final static byte EU_TYPE_MEDIUM = 1;
	final static byte EU_TYPE_EU = 2;
	final static byte EU_TYPE_RECEIVER = 3;
	
	private ArrayList<int[]> connectedElectricityReceivers = new ArrayList<int[]>();
	
    /**	The ItemStacks that hold the items in the PS. */
    private ItemStack[] inventoryItemStacks = new ItemStack[6];

    /** The number of ticks that the PS will keep burning. */
    public int powerStationBurnTime = 0;

    /**	The number of ticks that a fresh copy of the item would keep the PS burning for. */
    public int itemBurnTickCapacity = 0;

    /** The number of ticks that the current item has been cooking for */
    public int itemBurnTickQuantity = 0;
    
    /** The current amount of heat this PS has. */
    public int currentHeat = 20;
    
    /** The optimal amount of heat this PS can have. */
    public int optimalHeat = 1500;

    /**	Returns the number of slots in the inventory. */
    public int getSizeInventory() {
        return this.inventoryItemStacks.length;
    }
    
    /**	Returns the stack in slot i. */
    public ItemStack getStackInSlot(int slot) {
        return this.inventoryItemStacks[slot];
    }

    /**	Removes from a specified number of items from an inventory slot and returns the new stack. */
    public ItemStack decrStackSize(int slot, int numberToRemove) {
        if (this.inventoryItemStacks[slot] != null) {
            ItemStack itemStack;

            if (this.inventoryItemStacks[slot].stackSize <= numberToRemove) {
                itemStack = this.inventoryItemStacks[slot];
                this.inventoryItemStacks[slot] = null;
                return itemStack;
            } else {
                itemStack = this.inventoryItemStacks[slot].splitStack(numberToRemove);

                if (this.inventoryItemStacks[slot].stackSize == 0)
                    this.inventoryItemStacks[slot] = null;
                    
                return itemStack;
            }
        } else {
            return null;
        }
    }

    /**
     * 	When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * 	like when you close a workbench GUI.
     * 	@param slot = The slot being accessed by the method.
     */
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.inventoryItemStacks[slot] != null) {
            ItemStack itemStack = this.inventoryItemStacks[slot];
            this.inventoryItemStacks[slot] = null;
            return itemStack;
        } else {
            return null;
        }
    }

    /**
     * 	Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     * 	@param slot = The slot being accessed by the method.
     * 	@param itemStack = The itemStack to place into the slot.
     */
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        this.inventoryItemStacks[slot] = itemStack;

        if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
            itemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**	Returns the name of the inventory. */
    public String getInvName() {
        return "container.fossilfuelpowerstation";
    }

    /**
     * 	Reads a tile entity from NBT.
     * 	@param tagCompound = The NBTTagCompound to read variables from.
     */
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        NBTTagList tagList = tagCompound.getTagList("Items");
        this.inventoryItemStacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound newTagCompound = (NBTTagCompound)tagList.tagAt(i);
            byte slotByte = newTagCompound.getByte("Slot");

            if (slotByte >= 0 && slotByte < this.inventoryItemStacks.length) {
                this.inventoryItemStacks[slotByte] = ItemStack.loadItemStackFromNBT(newTagCompound);
            }
        }

        this.powerStationBurnTime = tagCompound.getShort("BurnTime");
        this.itemBurnTickQuantity = tagCompound.getShort("CookTime");
        this.itemBurnTickCapacity = getItemBurnTime(this.inventoryItemStacks[1]);
    }

    /**
     * 	Writes a tile entity to NBT.
     * 	@param tagCompound = The NBTTagCompound to write the variables to.
     */
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setShort("BurnTime", (short)this.powerStationBurnTime);
        tagCompound.setShort("CookTime", (short)this.itemBurnTickQuantity);
        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < this.inventoryItemStacks.length; ++i) {
            if (this.inventoryItemStacks[i] != null) {
                NBTTagCompound newTagCompound = new NBTTagCompound();
                newTagCompound.setByte("Slot", (byte)i);
                this.inventoryItemStacks[i].writeToNBT(newTagCompound);
                tagList.appendTag(newTagCompound);
            }
        }
        tagCompound.setTag("Items", tagList);
    }

    /**	Returns the maximum stack size for a inventory slot. */
    public int getInventoryStackLimit() {
        return 64;
    }


    /**
     *	Returns an integer between 0 and the passed value representing how close the current item is to being completely burned.
     *	@param value = The value to compare the progress to.
     */
    @SideOnly(Side.CLIENT)
    public int getCookProgressScaled(int value) {
        return this.itemBurnTickQuantity * value / 200;
    }

    /**
     * 	Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
     * 	item, where 0 means that the item is exhausted and the passed value means that the item is fresh
     * 	@param value = The value to compare the progress to.
     */
    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(int value) {
        if (this.itemBurnTickCapacity == 0) {
            this.itemBurnTickCapacity = 200;
        }
        return this.powerStationBurnTime * value / this.itemBurnTickCapacity;
    }

    /**	Returns true if the PS is currently burning. */
    public boolean isBurning() {
        return this.powerStationBurnTime > 0;
    }

    /**
     * 	Checks whether the block at the specified location is either an Electrical Unit or an Electrical Medium
     * 	@param x	The X value
     * 	@param y	The y value
     * 	@param z	The z value
     * 	@return	A byte that says what the block is. 0: nothing, 1: electrical unit, 2: electrical medium
     */
    public byte isElectricalUnit(int x, int y, int z) {
        Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
        if(block instanceof IElectricityReceiver)
        	return EU_TYPE_RECEIVER;
        if(block instanceof IElectricalUnit)
        	return EU_TYPE_EU;
        if(block instanceof IElectricalMedium)
        	return EU_TYPE_MEDIUM;
        return 0;
    }
    
    private ArrayList getConnections(int x, int y, int z, ForgeDirection directionToIgnore) {
    	ArrayList data = new ArrayList();
    	ArrayList<ArrayList> connections = new ArrayList();
    	
    	String testString = "";
    	
    	//If -X
    	if(!directionToIgnore.equals(ForgeDirection.WEST)) {
    		byte tempByte = isElectricalUnit(x-1, y, z);
    		if(tempByte != 0) {
        		int[] position = {x-1, y, z, tempByte};
        		data = new ArrayList();
    			data.add(position);
        		data.add(ForgeDirection.EAST);
        		connections.add(data);
    		}
    	}
    	//If +X
    	if(!directionToIgnore.equals(ForgeDirection.EAST)) {
    		byte tempByte = isElectricalUnit(x+1, y, z);
    		if(tempByte != 0) {
        		int[] position = {x+1, y, z, tempByte};
        		data = new ArrayList();
    			data.add(position);
        		data.add(ForgeDirection.WEST);
        		connections.add(data);
    		}
    	}
    	//If -Z
    	if(!directionToIgnore.equals(ForgeDirection.NORTH)) {
    		byte tempByte = isElectricalUnit(x, y, z-1);
    		if(tempByte != 0) {
        		int[] position = {x, y, z-1, tempByte};
        		data = new ArrayList();
    			data.add(position);
        		data.add(ForgeDirection.SOUTH);
        		testString += "-SOUTH (Z:" + z + ") (NewZ:" + ( ((int[]) data.get(0))[2] ) + ")";
        		connections.add(data);
    		}
    	}
    	//If +Z
    	if(!directionToIgnore.equals(ForgeDirection.SOUTH)) {
    		byte tempByte = isElectricalUnit(x, y, z+1);
    		if(tempByte != 0) {
        		int[] position = {x, y, z+1, tempByte};
        		data = new ArrayList();
    			data.add(position);
        		data.add(ForgeDirection.NORTH);
        		testString += "-NORTH (Z:" + z + ") (NewZ:" + ( ((int[]) data.get(0))[2] ) + ")";
        		connections.add(data);
    		}
    	}
    	//If -Y
    	if(!directionToIgnore.equals(ForgeDirection.DOWN)) {
    		byte tempByte = isElectricalUnit(x, y-1, z);
    		if(tempByte != 0) {
        		int[] position = {x, y-1, z, tempByte};
        		data = new ArrayList();
    			data.add(position);
        		data.add(ForgeDirection.UP);
        		connections.add(data);
    		}
    	}
    	//If +Y
    	if(!directionToIgnore.equals(ForgeDirection.UP)) {
    		byte tempByte = isElectricalUnit(x, y+1, z);
    		if(tempByte != 0) {
        		int[] position = {x, y+1, z, tempByte};
        		data = new ArrayList();
    			data.add(position);
        		data.add(ForgeDirection.DOWN);
        		connections.add(data);
    		}
    	}
    	return connections;
    }
    
    private void transferEnergy() {
    	//ArrayList energyReceivers = getEnergyDestinations();
    	System.out.println("!!!TRANSFERRING ENERGY!!!");
    	for(int i= 0; i<connectedElectricityReceivers.size(); i++) {
    		int[] connectionPos = (int[]) connectedElectricityReceivers.get(i);
    		System.out.println("Sending Energy to x:" + connectionPos[0] + ", y:" + connectionPos[1] + ", z:" + connectionPos[2]);
    	}
    	
    	for(int i = 0; i<connectedElectricityReceivers.size(); i++) {
    		int[] connectionPos = (int[]) connectedElectricityReceivers.get(i);
    		int blockId = worldObj.getBlockId(connectionPos[0], connectionPos[1], connectionPos[2]);
    		Block block = Block.blocksList[blockId];
    		if(block instanceof IElectricityReceiver) {
    			((IElectricityReceiver) block).receiveEnergy(worldObj, connectionPos[0], connectionPos[1], connectionPos[2], 10);
    		}
    	}
    	
    }
    
    public ArrayList getEnergyDestinations() {
    	//Get startpoint
    	ArrayList <ArrayList>connections = getConnections(xCoord, yCoord, zCoord, ForgeDirection.UNKNOWN);
    	ArrayList <ArrayList>tempConnections;// = new ArrayList();
    	ArrayList energyDestinations = new ArrayList();	
    	int[] connectionPos;
    	
    	while(connections.size() > 0) {
    		tempConnections = new ArrayList();
    		
    		for(int i = 0; i<connections.size(); i++) {
    			connectionPos = (int[]) connections.get(i).get(0);
    			if(connectionPos[3] == EU_TYPE_RECEIVER) {
    				energyDestinations.add(connectionPos);
    			} else {
    				tempConnections.addAll(getConnections(connectionPos[0], connectionPos[1], connectionPos[2], (ForgeDirection)connections.get(i).get(1)));
    			}
    		}

    		
    		if(tempConnections.size() > 0)
    			connections = tempConnections;
    		else
    			connections.clear();
    	}
    	return energyDestinations;
    }
    
    /**	Updates the entity's state. */
    public void updateEntity() {
    	boolean inventoryChanged = false;
        boolean isBurning = this.powerStationBurnTime > 0;
        
        if (isBurning) {
            --this.powerStationBurnTime;
            currentHeat = currentHeat +5;
//            System.out.println("Current heat: " + currentHeat + "°C");
        } else {
        	--currentHeat;
        }
        
        if(currentHeat > optimalHeat) {
        	transferEnergy();
        }
        
        if(currentHeat > 2000)
        	currentHeat = 2000;
        
        if (!this.worldObj.isRemote) {
        	if(this.powerStationBurnTime == 0 && canBurnFuel()) {
//        		System.out.println("ItemBurnTime: " + getItemBurnTime(this.inventoryItemStacks[0]));
        		this.itemBurnTickCapacity = this.powerStationBurnTime = getItemBurnTime(this.inventoryItemStacks[0]);
        		decrStackSize(0, 1);
        		inventoryChanged = true;
        	}
        	if(inventoryItemStacks[0] == null) {
        		for(int i = 1; i < 6; i++) {
        			if(inventoryItemStacks[i] != null) {
        				inventoryItemStacks[0] = inventoryItemStacks[i];
        				inventoryItemStacks[i] = null;
        				return;
        			}
        		}
        		
        	}
        	
        	
        	
        	
            if (this.powerStationBurnTime == 0 && this.canSmelt()) {
                this.itemBurnTickCapacity = this.powerStationBurnTime = getItemBurnTime(this.inventoryItemStacks[1]);

                if (this.powerStationBurnTime > 0) {
                    inventoryChanged = true;

                    if (this.inventoryItemStacks[1] != null) {
                        --this.inventoryItemStacks[1].stackSize;

                        if (this.inventoryItemStacks[1].stackSize == 0) {
                            this.inventoryItemStacks[1] = this.inventoryItemStacks[1].getItem().getContainerItemStack(inventoryItemStacks[1]);
                        }
                    }
                }
            }

            if (this.isBurning() && this.canSmelt()) {
                ++this.itemBurnTickQuantity;

                if (this.itemBurnTickQuantity == 200) {
                    this.itemBurnTickQuantity = 0;
                    this.smeltItem();
                    inventoryChanged = true;
                }
            } else {
                this.itemBurnTickQuantity = 0;
            }

            if (isBurning != this.powerStationBurnTime > 0) {
                inventoryChanged = true;
                BlockFossilFuelPS.updatePSBlockState(this.powerStationBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            }
        }

        if (inventoryChanged) {
            this.onInventoryChanged();
        }
    }

    /**	Returns true if the PS can burn the item currently in the burning slot for energy. */
    private boolean canBurnFuel() {
    	
    	if(this.inventoryItemStacks[0] == null) {
    		return false;
    	} else if(!isItemFuel(inventoryItemStacks[0])) {
    		return false;
    	} else {
    		return true;
    	}
    }
    /**
     * Returns true if the PS can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canSmelt()
    {
    	return false;
//        if (this.furnaceItemStacks[0] == null)
//        {
//            return false;
//        }
//        else
//        {
//            ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);
//            if (var1 == null) return false;
//            if (this.furnaceItemStacks[2] == null) return true;
//            if (!this.furnaceItemStacks[2].isItemEqual(var1)) return false;
//            int result = furnaceItemStacks[2].stackSize + var1.stackSize;
//            return (result <= getInventoryStackLimit() && result <= var1.getMaxStackSize());
//        }
    }

    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
     */
    public void smeltItem()
    {
//        if (this.canSmelt())
//        {
//            ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);
//
//            if (this.furnaceItemStacks[2] == null)
//            {
//                this.furnaceItemStacks[2] = var1.copy();
//            }
//            else if (this.furnaceItemStacks[2].isItemEqual(var1))
//            {
//                furnaceItemStacks[2].stackSize += var1.stackSize;
//            }
//
//            --this.furnaceItemStacks[0].stackSize;
//
//            if (this.furnaceItemStacks[0].stackSize <= 0)
//            {
//                this.furnaceItemStacks[0] = null;
//            }
//        }
    }

    /**
     * Returns the number of ticks that the supplied fuel item will keep the burning. (0 = Item is not fuel.)
     * @param itemStack = The itemstack containing the item that will be burned.
     */
    public static int getItemBurnTime(ItemStack itemStack) {
        if (itemStack == null) {
            return 0;
        } else {
            int itemShiftedIndex = itemStack.getItem().shiftedIndex;
            Item item = itemStack.getItem();

            if (item instanceof ItemBlock && Block.blocksList[itemShiftedIndex] != null) {
                Block block = Block.blocksList[itemShiftedIndex];

                if (block == Block.woodSingleSlab) return 150;
                if (block.blockMaterial == Material.wood) return 300;
            }
            if (item instanceof ItemTool && ((ItemTool) item).func_77861_e().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword) item).func_77825_f().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe) item).func_77842_f().equals("WOOD")) return 200;
            if (itemShiftedIndex == Item.stick.shiftedIndex) return 100;
            if (itemShiftedIndex == Item.coal.shiftedIndex) return 1600;
            if (itemShiftedIndex == Item.bucketLava.shiftedIndex) return 20000;
            if (itemShiftedIndex == Block.sapling.blockID) return 100;
            if (itemShiftedIndex == Item.blazeRod.shiftedIndex) return 2400;
            return GameRegistry.getFuelValue(itemStack);
        }
    }

    /**
     * 	Checks whether the passed along item is fuel, returns outcome.
     * 	@param itemStack = The item stack containing the item to check.
     */
    public static boolean isItemFuel(ItemStack itemStack) {
        return getItemBurnTime(itemStack) > 0;
    }

    /**
     * 	Checks whether this tileentity is useable by the player.
     * 	@param entityPlayer = The player.
     */
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : entityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public int getStartInventorySide(ForgeDirection side) {
        return 0;
    }

    @Override
    public int getSizeInventorySide(ForgeDirection side) {
        return 1;
    }

	@Override
	public void openChest() {
		//Nothing, this is no chest!
	}

	@Override
	public void closeChest() {
		//Nothing, this is no chest!
	}
	
	public void updateValidReceivers(World world, int x, int y, int z) {
		setConnectedElectricityReceivers(
				ElectricConnectionManager.getEnergyDestinations(world, x, y, z)
//				getEnergyDestinations()
				);
	}
	
	public ArrayList<int[]> getConnectedElectricityReceivers() {
		return connectedElectricityReceivers;
	}
	
	public void setConnectedElectricityReceivers(ArrayList<int[]> connectedElectricityReceivers) {
		this.connectedElectricityReceivers = connectedElectricityReceivers;
		System.out.println(this.connectedElectricityReceivers);
	}
}
