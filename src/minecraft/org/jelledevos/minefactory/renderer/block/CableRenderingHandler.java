package org.jelledevos.minefactory.renderer.block;

import org.jelledevos.minefactory.MineFactory;
import org.jelledevos.minefactory.block.cable.BlockCable;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CableRenderingHandler implements ISimpleBlockRenderingHandler {
	
	private int renderTypeId = MineFactory.instance.renderTypeCableId;

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		
		BlockCable cableBlock = (BlockCable) block;
		
        boolean success = false;
        float minSize = 0.375F;
        float maxSize = 0.625F;
        cableBlock.setBlockBounds(minSize, 0.0F, minSize, maxSize, 1.0F, maxSize);
        renderer.renderStandardBlock(cableBlock, x, y, z);
        success = true;
        boolean canConnectOnXAxis = false;
        boolean canConnectOnZAxis = false;

        
        boolean canConnectCableToNeighbor1 = cableBlock.canConnectCableTo(world, x - 1, y, z);
        boolean canConnectCableToNeighbor2 = cableBlock.canConnectCableTo(world, x + 1, y, z);
        boolean canConnectCableToNeighbor3 = cableBlock.canConnectCableTo(world, x, y, z - 1);
        boolean canConnectCableToNeighbor4 = cableBlock.canConnectCableTo(world, x, y, z + 1);
        
        if (canConnectCableToNeighbor1 || canConnectCableToNeighbor2) {
            canConnectOnXAxis = true;
        }

        if (canConnectCableToNeighbor3 || canConnectCableToNeighbor4) {
            canConnectOnZAxis = true;
        }

        if (!canConnectOnXAxis && !canConnectOnZAxis) {
            canConnectOnXAxis = true;
        }

        minSize = 0.4375F;
        maxSize = 0.5625F;
        float minY = 0.75F;
        float maxY = 0.9375F;
        float minX = canConnectCableToNeighbor1 ? 0.0F : minSize;
        float maxX = canConnectCableToNeighbor2 ? 1.0F : maxSize;
        float minZ = canConnectCableToNeighbor3 ? 0.0F : minSize;
        float maxZ = canConnectCableToNeighbor4 ? 1.0F : maxSize;

        if (canConnectOnXAxis) {
            cableBlock.setBlockBounds(minX, minY, minSize, maxX, maxY, maxSize);
            renderer.renderStandardBlock(cableBlock, x, y, z);
            success = true;
        }

        if (canConnectOnZAxis) {
            cableBlock.setBlockBounds(minSize, minY, minZ, maxSize, maxY, maxZ);
            renderer.renderStandardBlock(cableBlock, x, y, z);
            success = true;
        }

        minY = 0.375F;
        maxY = 0.5625F;

        if (canConnectOnXAxis) {
            cableBlock.setBlockBounds(minX, minY, minSize, maxX, maxY, maxSize);
            renderer.renderStandardBlock(cableBlock, x, y, z);
            success = true;
        }

        if (canConnectOnZAxis) {
            cableBlock.setBlockBounds(minSize, minY, minZ, maxSize, maxY, maxZ);
            renderer.renderStandardBlock(cableBlock, x, y, z);
            success = true;
        }

        cableBlock.setBlockBoundsBasedOnState(world, x, y, z);
        return success;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getRenderId() {
		return renderTypeId;
	}

}
