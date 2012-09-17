package org.jelledevos.minefactory.interfaces;

import net.minecraft.src.World;

public interface IElectricityAccumulator {
	public void receiveEnergy(World world, int x, int y, int z, int i);
}
