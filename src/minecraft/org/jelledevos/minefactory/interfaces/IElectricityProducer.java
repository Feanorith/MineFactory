package org.jelledevos.minefactory.interfaces;

import net.minecraft.src.World;

public interface IElectricityProducer {
	
	public void updateValidReceivers(World world, int x, int y, int z);

}
