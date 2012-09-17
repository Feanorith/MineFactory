package org.jelledevos.minefactory;

import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class MFPacketHandler implements IPacketHandler {
	
	@Override
	public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player player) {
		// TODO: Add packet handling.
	}
}
