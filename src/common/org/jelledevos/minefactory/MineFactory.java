package org.jelledevos.minefactory;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import org.jelledevos.minefactory.block.BlockHelloWorld;
import org.jelledevos.minefactory.block.accumulator.BlockAccumulator;
import org.jelledevos.minefactory.block.cable.BlockCable;
import org.jelledevos.minefactory.block.powerstation.BlockFossilFuelPS;
import org.jelledevos.minefactory.gui.MFGuiHandler;
import org.jelledevos.minefactory.renderer.block.CableRenderingHandler;
import org.jelledevos.minefactory.tileentity.TileEntityAccumulator;
import org.jelledevos.minefactory.tileentity.TileEntityFossilFuelPS;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.TileEntity;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(
		modid = "MineFactory",
		name = "MineFactory",
		version = "In-Dev 1.0")
@NetworkMod(
		channels = { "MineFactory" },
		clientSideRequired = true,
		serverSideRequired = true,
		packetHandler = MFPacketHandler.class )
public class MineFactory {
	
	@Instance
	public static MineFactory instance;
	@SidedProxy(
			clientSide = "org.jelledevos.minefactory.ClientProxy",
			serverSide = "org.jelledevos.minefactory.CommonProxy")
	public static CommonProxy proxy;
	
	//Block definitions
	public Block blockHelloWorld;
	public Block blockFossilFuelPSActive;
	public Block blockFossilFuelPSIdle;
	public Block blockCable;
	public Block blockAccumulator;
	
	//Block Id definitions;
	public int blockHelloWorldId;
	public int blockFossilFuelPSActiveId;
	public int blockFossilFuelPSIdleId;
	public int blockCableId;
	public int blockAccumulatorId;
	
	//Renderer ID definitions
	public int renderTypeCableId;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		// Add Pre-Initialization code such as configuration loading
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		
		config.load();

		//Block ids
		blockHelloWorldId = config.getOrCreateBlockIdProperty("BlockHelloWorld", 160).getInt();
		blockFossilFuelPSActiveId = config.getOrCreateBlockIdProperty("BlockFossilFuelPSActive", 161).getInt();
		blockFossilFuelPSIdleId = config.getOrCreateBlockIdProperty("BlockFossilFuelPSIdle", 162).getInt();
		blockCableId = config.getOrCreateBlockIdProperty("BlockCable", 163).getInt();
		blockAccumulatorId = config.getOrCreateBlockIdProperty("BlockAccumulatorId", 164).getInt();
		
		//Renderer ids
		renderTypeCableId = RenderingRegistry.getNextAvailableRenderId();//config.getOrCreateIntProperty("RenderTypeCable", Configuration.CATEGORY_GENERAL, 20).getInt();
		
		config.save();
	}

	@Init
	public void init(FMLInitializationEvent evt) {
		proxy.registerRenderInformation();
		addBlocks();
		addRecipes();
		addTileEntities();
		addGUIs();
	}
	
	private void addGUIs() {
		NetworkRegistry.instance().registerGuiHandler(this, new MFGuiHandler());
	}
	
	private void addTileEntities() {
		GameRegistry.registerTileEntity(TileEntityFossilFuelPS.class, "fossilFuelPS");
		GameRegistry.registerTileEntity(TileEntityAccumulator.class, "accumulator");
		
	}
	
	private void addBlocks() {
		//HelloWorldBlock
		blockHelloWorld = new BlockHelloWorld(blockHelloWorldId);
		GameRegistry.registerBlock(blockHelloWorld);
		LanguageRegistry.addName(blockHelloWorld, "Hello World Block");
		
		//FossilFuel PowerStation Blocks
		blockFossilFuelPSIdle = new BlockFossilFuelPS(blockFossilFuelPSIdleId, false);
		GameRegistry.registerBlock(blockFossilFuelPSIdle);
		LanguageRegistry.addName(blockFossilFuelPSIdle, "Fossil-Fuel Power Station (Idle)");
	
		blockFossilFuelPSActive = new BlockFossilFuelPS(blockFossilFuelPSActiveId, true);
		GameRegistry.registerBlock(blockFossilFuelPSActive);
		LanguageRegistry.addName(blockFossilFuelPSActive, "Fossil-Fuel Power Station");
		
		//Accumulator Blocks
		blockAccumulator = new BlockAccumulator(blockAccumulatorId);
		GameRegistry.registerBlock(blockAccumulator);
		LanguageRegistry.addName(blockAccumulator, "Energy Accumulator");
		
		//Cable Block
		blockCable = new BlockCable(blockCableId, 1);
		GameRegistry.registerBlock(blockCable);
		LanguageRegistry.addName(blockCable, "Cable");
	}
	
	private void addRecipes() {
		//Utility recipes
		GameRegistry.addRecipe(new ItemStack(Block.wood, 64), new Object[] {
			"XX", 'X', Block.wood
			});
		GameRegistry.addRecipe(new ItemStack(blockHelloWorld), new Object[] {
			"XX", 'X', Block.dirt
			});
		
		//Power Station recipes
		GameRegistry.addRecipe(new ItemStack(blockFossilFuelPSIdle, 1), new Object[] {
			"X", 'X', Block.dirt
			});
		
		//Cable recipes
		GameRegistry.addRecipe(new ItemStack(blockCable, 64), new Object[] {
			"XX", "XX", 'X', Block.wood
		});
		
		//Accumulator recipes
		GameRegistry.addRecipe(new ItemStack(blockAccumulator, 64), new Object[] {
			"X", 'X', blockCable
		});
	}
	

	@PostInit
	public void postInit(FMLPostInitializationEvent evt) {
		// Add Post-Initialization code such as mod hooks
	}
	
}