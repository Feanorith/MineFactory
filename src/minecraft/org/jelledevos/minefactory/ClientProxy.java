package org.jelledevos.minefactory;

import org.jelledevos.minefactory.CommonProxy;
import org.jelledevos.minefactory.renderer.block.CableRenderingHandler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
	
	private void initProxy() {

	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderInformation() {
		//MinecraftForgeClient.preloadTexture("/lightningcraft.png");
		RenderingRegistry.registerBlockHandler(new CableRenderingHandler());
	}

	@Override
	public void registerTileEntitySpecialRenderer(/*PLACEHOLDER*/) {
		
	}

	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}
}