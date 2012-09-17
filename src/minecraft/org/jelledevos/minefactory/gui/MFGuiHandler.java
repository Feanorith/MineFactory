package org.jelledevos.minefactory.gui;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;

import org.jelledevos.minefactory.container.ContainerAccumulator;
import org.jelledevos.minefactory.container.ContainerFossilFuelPS;
import org.jelledevos.minefactory.tileentity.TileEntityAccumulator;
import org.jelledevos.minefactory.tileentity.TileEntityFossilFuelPS;

public class MFGuiHandler implements IGuiHandler {

    /**
     * 	Returns an instance of the GUI's container.
     */
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        
        if(tileEntity instanceof TileEntityFossilFuelPS)
        	return new ContainerFossilFuelPS(player.inventory, (TileEntityFossilFuelPS) tileEntity);
        if(tileEntity instanceof TileEntityAccumulator)
        	return new ContainerAccumulator(player.inventory, (TileEntityAccumulator) tileEntity);
        return null;
	}

	/**
	 * 	Returns an instance of the GUI.
	 */
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        
        if(tileEntity instanceof TileEntityFossilFuelPS)
        	return new GuiFossilFuelPS(player.inventory, (TileEntityFossilFuelPS) tileEntity);
        if(tileEntity instanceof TileEntityAccumulator)
        	return new GuiAccumulator(player.inventory, (TileEntityAccumulator) tileEntity);
        return null;
	}
	
}
