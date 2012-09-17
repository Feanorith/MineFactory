package org.jelledevos.minefactory.block.cable;

import java.util.ArrayList;

import org.jelledevos.minefactory.ElectricConnectionManager;
import org.jelledevos.minefactory.MineFactory;
import org.jelledevos.minefactory.interfaces.IElectricalMedium;
import org.jelledevos.minefactory.interfaces.IElectricalUnit;
import org.jelledevos.minefactory.interfaces.IElectricityWiring;
import org.jelledevos.minefactory.tileentity.TileEntityPowerStation;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.World;

public class BlockCable extends Block implements IElectricalMedium, IElectricityWiring {
	
	private int renderType = MineFactory.instance.renderTypeCableId;

    public BlockCable(int blockId, int textureIndex) {
        super(blockId, textureIndex, Material.wood);
    }

    /**
     * 	Returns a bounding box from the pool of bounding boxes.
     * 	(This means this box can change after the pool has been cleared to be reused.)
     * 	@param	world	The world object for block access.
     * 	@param	x		This block's X value.
     * 	@param	y		This block's Y value.
     * 	@param	z		This block's Z value.
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        float minX = 0.375F;
        float maxX = 0.625F;
        float minZ = 0.375F;
        float maxZ = 0.625F;

        if (canConnectCableTo(world, x, y, z - 1))
            minZ = 0.0F;
        if (canConnectCableTo(world, x, y, z + 1))
            maxZ = 1.0F;
        if (canConnectCableTo(world, x - 1, y, z))
            minX = 0.0F;
        if (canConnectCableTo(world, x + 1, y, z))
            maxX = 1.0F;

        return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)((float)x + minX), (double)y, (double)((float)z + minZ), (double)((float)x + maxX), (double)((float)y + 1.5F), (double)((float)z + maxZ));
    }

    /**
     * 	Updates the blocks bounds based on its current state.
     * 	@param world	The world object for block access.
     * 	@param x		This block's X value.
     * 	@param y		This block's Y value.
     * 	@param z		This block's Z value.
     */
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {     
        float minX = 0.375F;
        float maxX = 0.625F;
        float minZ = 0.375F;
        float maxZ = 0.625F;

        if (canConnectCableTo(world, x, y, z - 1))
            minZ = 0.0F;
        if (canConnectCableTo(world, x, y, z + 1))
            maxZ = 1.0F;
        if (canConnectCableTo(world, x - 1, y, z))
            minX = 0.0F;
        if (canConnectCableTo(world, x + 1, y, z))
            maxX = 1.0F;

        this.setBlockBounds(minX, 0.0F, minZ, maxX, 1.0F, maxZ);
    }

    /**
     * 	Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * 	adjacent blocks and also whether the player can attach torches, redstone wire, etc. to this block.
     * 	@return	(boolean) True of this opaque and a full 1m cube.
     */
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * @return	(boolean) False if this block does not render like an ordinary block.
     * (Examples: signs, buttons, stairs, ...)
     */
    public boolean renderAsNormalBlock() {
        return false;
    }

    
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    /**
     * 	@return	(int) The type of render function that is called for this block.
     */
    public int getRenderType() {
        return renderType;
    }

    /**
     * 	@param	world	The world object for block access.
     * 	@param	x		The specified block's X value.
     * 	@param	y		The specified block's Y value.
     * 	@param	z		The specified block's Z value.
     * 	@return			(boolean) True if the the specified block can be connected to a cable.
     */
    public boolean canConnectCableTo(IBlockAccess world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        
        if( block instanceof IElectricalUnit || block instanceof IElectricalMedium ) {
        	return true;
        } else {
        	return false;
        }
    }

    /**
     * 	@param	blockId	The provided blockId.
     * 	@return			(boolean) True if the provided block id belongs to a cable block.
     */
    public static boolean isIdACable(int blockId) {
        return blockId == MineFactory.instance.blockCableId;
    }
    

    /**
     * 	Lets the block know when one of its neighbor changes. In wiring, this finds all connected electricity
     * 	producers and tells them to reevaluate their wiring for valid electricity receivers.
     * 	@param world			The world object for block access.
     * 	@param x				The neighbor's X value.
     * 	@param y				The neighbor's Y value.
     * 	@param z				The neighbor's Z value.
     * 	@param neighborBlockId	The neighbor's blockId.
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockId) {
    	if(neighborBlockId == MineFactory.instance.blockCableId) {
    		ArrayList<int[]> list = ElectricConnectionManager.getConnectedProducers(world, x, y, z);
    		for(int[] array : list) {
    			TileEntityPowerStation tileEntity = (TileEntityPowerStation) world.getBlockTileEntity(array[0], array[1], array[2]);
//    			tileEntity.updateValidReceivers(world, array[0], array[1], array[2]);
    			tileEntity.updateValidReceivers(world, array[0], array[1], array[2]);
    		}
    	}
    	
    }
    
    /**
     *	Called right before this block is destroyed by a player.
     *	@param world	The world object for block access.
     *	@param x		This block's X value. 
     *	@param y		This block's Y value.
     *	@param z		This block's Z value.
     *	@param metadata	This block's metadata.
     */
    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata) {
    	world.notifyBlocksOfNeighborChange(x, y, z, blockID);
    	super.onBlockDestroyedByPlayer(world, x, y, z, metadata);
    }
    
    /**
     *	Called upon the block being destroyed by an explosion.
     *	@param world	The world object for block access.
     *	@param x		This block's X value. 
     *	@param y		This block's Y value.
     *	@param z		This block's Z value.
     */
    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z) {
    	world.notifyBlocksOfNeighborChange(x, y, z, blockID);
    	super.onBlockDestroyedByExplosion(world, x, y, z);
    }
}

