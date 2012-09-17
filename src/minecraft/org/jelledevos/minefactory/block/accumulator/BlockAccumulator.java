package org.jelledevos.minefactory.block.accumulator;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.StepSound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

import org.jelledevos.minefactory.MineFactory;
import org.jelledevos.minefactory.interfaces.IElectricalUnit;
import org.jelledevos.minefactory.interfaces.IElectricityAccumulator;
import org.jelledevos.minefactory.tileentity.TileEntityAccumulator;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class BlockAccumulator extends BlockContainer implements IElectricalUnit, IElectricityAccumulator {

	    /**
	     *	The ONLY constructor the this block.
	     *	@param blockID = The block id this block will be registered under.
	     */
		public BlockAccumulator(int blockID)
		{
			super(blockID, Material.rock);
			
		    blockIndexInTexture = 45;
		    setBlockName("accumulator");
		    setHardness(0.5F);
		    
		    setStepSound(new StepSound("stone", 1.0F, 1.5F));
		    
		    setRequiresSelfNotify();
		    //setResistance(5.0f);
		    
		}
		
	    /**
	     *	Called upon block activation. (Right click on the block.)
	     *	@param entityPlayer = The player.
	     *	@param direction = Either the side the player was facing when he clicked the block, either the side of the block clicked.
	     * 	@param world = The world object.
	     *  @param x = The block's x position.
	     *	@param y = The block's y position.
	     *	@param z = The block's z position.
	     *	@param xOffset = 
	     *	@param yOffset =
	     *	@param zOffset =
	     */
	    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int direction, float xOffset, float yOffset, float zOffset) {
	        if (world.isRemote) {
	            return true;
	        } else {
	            TileEntityAccumulator entity = (TileEntityAccumulator)world.getBlockTileEntity(x, y, z);
	            //TODO 
	            entity.accumulatorCurrentStorage = 0;
	            if (entity != null)
	            	entityPlayer.openGui(MineFactory.instance, 0, world, x, y, z);
	            return true;
	        }
	    }
		
		@Override
		/**	Grabs the current texture file used for this block. */
		public String getTextureFile() {
		        return "/terrain.png";
		}
		
	    /**
	     * 	Returns the ID of the items to drop on destruction.
	     * 	@param metadata = Destroyed block's metadata.
	     * 	@param random = Random number generator.
	     * 	@param fortune = Current item fortune level.
	     */
	    public int idDropped(int metadata, Random random, int fortune) {
	        return MineFactory.instance.blockAccumulator.blockID;
	    }
	    
	    /**
	     * 	Called whenever the block is added into the world.
	     * 	@param world = The world object.
	     *  @param x = The block's x position.
	     *	@param y = The block's y position.
	     *	@param z = The block's z position.
	     */
	    public void onBlockAdded(World world, int x, int y, int z)
	    {
	        super.onBlockAdded(world, x, y, z);
	        this.setDefaultDirection(world, x, y, z);
	    }
	    
	    /**
	     * 	Sets the block's default direction.
	     * 	@param world = The world object.
	     *  @param x = The block's x position.
	     *	@param y = The block's y position.
	     *	@param z = The block's z position.
	     */
	    private void setDefaultDirection(World world, int x, int y, int z) {
	        if (!world.isRemote) {
	            int horizontalNeighbor1ID = world.getBlockId(x, y, z - 1);
	            int horizontalNeighbor2ID = world.getBlockId(x, y, z + 1);
	            int horizontalNeighbor3ID = world.getBlockId(x - 1, y, z);
	            int horizontalNeighbor4ID = world.getBlockId(x + 1, y, z);
	            byte defaultDirection = 3;

	            if (Block.opaqueCubeLookup[horizontalNeighbor1ID] && !Block.opaqueCubeLookup[horizontalNeighbor2ID])
	                defaultDirection = 3;

	            if (Block.opaqueCubeLookup[horizontalNeighbor2ID] && !Block.opaqueCubeLookup[horizontalNeighbor1ID])
	                defaultDirection = 2;

	            if (Block.opaqueCubeLookup[horizontalNeighbor3ID] && !Block.opaqueCubeLookup[horizontalNeighbor4ID])
	                defaultDirection = 5;

	            if (Block.opaqueCubeLookup[horizontalNeighbor4ID] && !Block.opaqueCubeLookup[horizontalNeighbor3ID])
	                defaultDirection = 4;

	            world.setBlockMetadataWithNotify(x, y, z, defaultDirection);
	        }
	    }
	    

	    /**
	     * 	Retrieves the block texture to use based on the display side.
	     * 	@param side = The side to get the texture for.
	     */
	    public int getBlockTextureFromSide(int side) {
	        return side == 1 ? this.blockIndexInTexture + 17 : (side == 0 ? this.blockIndexInTexture + 17 : (side == 3 ? this.blockIndexInTexture - 1 : this.blockIndexInTexture));
	    }
	    
	    /**
	     * 	Retrieves the block texture to use based on the display side, position & variables.
	     * 	@param blockAccess = IBlockAccess object, can be used to get variables like metadata.
	     *	@param side = The side to get the texture for.	
	     *	@param x = The block's x position.
	     *	@param y = The block's y position.
	     *	@param z = The block's z position.
	     */
	    @SideOnly(Side.CLIENT)
	    public int getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
	        if (side == 1) {
	            return this.blockIndexInTexture + 17;
	        } else if (side == 0) {
	            return this.blockIndexInTexture + 17;
	        } else {
	            int blockMetadata = blockAccess.getBlockMetadata(x, y, z);
	            return side != blockMetadata ? this.blockIndexInTexture : this.blockIndexInTexture + 16;
	        }
	    }
	    
	    /**	Creates a TileEntity for the block. */
	    public TileEntity createNewTileEntity(World world) {
	        return new TileEntityAccumulator();
	    }
	    
	    /**
	     * 	Called when the block is placed in the world.
	     *  @param entity = The entity that placed the block.
	     * 	@param world = The world object.
	     *  @param x = The block's x position.
	     *	@param y = The block's y position.
	     *	@param z = The block's z position.
	     */
	    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity)
	    {
	        int lookingDirection = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

	        if (lookingDirection == 0)
	            world.setBlockMetadataWithNotify(x, y, z, 2);

	        if (lookingDirection == 1)
	            world.setBlockMetadataWithNotify(x, y, z, 5);

	        if (lookingDirection == 2)
	            world.setBlockMetadataWithNotify(x, y, z, 3);

	        if (lookingDirection == 3)
	            world.setBlockMetadataWithNotify(x, y, z, 4);
	    }
	    
	    /**
	     *	Ejects contained items into the world, and notifies neighbors of an update, as appropriate.
	     *	@param blockId = The block's blockId.
	     * 	@param blockMetadata = The block's metadata.
	     * 	@param world = The world object.
	     *  @param x = The block's x position.
	     *	@param y = The block's y position.
	     *	@param z = The block's z position.
	     */
	    public void breakBlock(World world, int x, int y, int z, int blockId, int blockMetadata) {
	        super.breakBlock(world, x, y, z, blockId, blockMetadata);
	    }

		@Override
		public void receiveEnergy(World world, int x, int y, int z, int i) {
			TileEntityAccumulator tileEntity = (TileEntityAccumulator) world.getBlockTileEntity(x, y, z);
			tileEntity.receiveEnergy(i);
		}

		
}

