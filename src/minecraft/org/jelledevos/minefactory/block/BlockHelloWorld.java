package org.jelledevos.minefactory.block;

import net.minecraft.src.Block;
import net.minecraft.src.Material;

public class BlockHelloWorld extends Block {
	
	public BlockHelloWorld(int blockID)
	{
	        super(blockID, Material.wood);
	        
	        blockIndexInTexture = 1;
	        setHardness(2.0f);
	        setResistance(5.0f);
	        setStepSound(soundWoodFootstep);

	        setBlockName("Hello World Block");
	}
	
	@Override
	public String getTextureFile()
	{
	        return "/terrain.png";
	}
	
	@Override
	public int getBlockTextureFromSide(int side)
	{
	        switch(side)
	        {
	        case 0:
	                return 21;
	        case 1:
	                return 21;
	        }
	        return blockIndexInTexture;
	}


}
