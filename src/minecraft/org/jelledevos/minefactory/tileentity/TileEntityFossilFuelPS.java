package org.jelledevos.minefactory.tileentity;

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
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

import org.jelledevos.minefactory.block.powerstation.BlockFossilFuelPS;
import org.jelledevos.minefactory.interfaces.IElectricalUnit;
import org.jelledevos.minefactory.interfaces.IElectricalMedium;
import org.jelledevos.minefactory.interfaces.IElectricityReceiver;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntityFossilFuelPS extends TileEntityPowerStation
{
	
}

