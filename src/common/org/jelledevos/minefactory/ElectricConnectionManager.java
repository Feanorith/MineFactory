package org.jelledevos.minefactory;

import java.util.ArrayList;

import org.jelledevos.minefactory.interfaces.IElectricalMedium;
import org.jelledevos.minefactory.interfaces.IElectricalUnit;
import org.jelledevos.minefactory.interfaces.IElectricityAccumulator;
import org.jelledevos.minefactory.interfaces.IElectricityConsumer;
import org.jelledevos.minefactory.interfaces.IElectricityProducer;
import org.jelledevos.minefactory.interfaces.IElectricityReceiver;
import org.jelledevos.minefactory.interfaces.IElectricityWiring;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;

public class ElectricConnectionManager {
	
	/**	
	 * 	These constants identify the different kinds of electrical units.
	 * 	0: nothing, 1: wiring, 2: an energy producer, 3: an energy consumer, 4: an energy accumulator.
	 */ 
	final static byte EU_TYPE_NOTHING = 0;
	final static byte EU_TYPE_WIRING = 1;
	final static byte EU_TYPE_PRODUCER = 2;
	final static byte EU_TYPE_CONSUMER = 3;
	final static byte EU_TYPE_ACCUMULATOR = 4;
	
	/**
	 * This method is used by Cables to find any attached electricity producers, so it can tell them to update.
	 * (This happens when the cable-wiring has changed.)
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public static ArrayList<int[]> getConnectedProducers(World world, int x, int y, int z) {
		ArrayList<ArrayList> connections;
		ArrayList<ArrayList> tempConnections;
    	ArrayList<int[]> energyDestinations = new ArrayList();	
    	int[] connectionPos;
    	
    	//Get startpoint
    	connections = getConnections(world, x, y, z, ForgeDirection.UNKNOWN);

    	
    	while(connections.size() > 0) {
    		tempConnections = new ArrayList();
    		
    		for(int i = 0; i<connections.size(); i++) {
    			connectionPos = (int[]) connections.get(i).get(0);
    			if(connectionPos[3] == EU_TYPE_PRODUCER) {
    				energyDestinations.add(connectionPos);
    			} else {
    				tempConnections.addAll(getConnections(world, connectionPos[0], connectionPos[1], connectionPos[2], (ForgeDirection)connections.get(i).get(1)));
    			}
    		}

    		if(tempConnections.size() > 0)
    			connections = tempConnections;
    		else
    			connections.clear();
    	}	

    	return energyDestinations;
	}
	
	
    public static ArrayList getConnections(World world, int x, int y, int z, ForgeDirection directionToIgnore) {
    	ArrayList data = new ArrayList();
    	ArrayList<ArrayList> connections = new ArrayList();
    	
    	String testString = "";
    	
    	//If -X
    	if(!directionToIgnore.equals(ForgeDirection.WEST)) {
    		byte tempByte = isElectricalUnit(world, x-1, y, z);
    		if(tempByte != 0) {
        		int[] position = {x-1, y, z, tempByte};
        		data = new ArrayList();
    			data.add(position);
        		data.add(ForgeDirection.EAST);
        		connections.add(data);
    		}
    	}
    	//If +X
    	if(!directionToIgnore.equals(ForgeDirection.EAST)) {
    		byte tempByte = isElectricalUnit(world, x+1, y, z);
    		if(tempByte != 0) {
        		int[] position = {x+1, y, z, tempByte};
        		data = new ArrayList();
    			data.add(position);
        		data.add(ForgeDirection.WEST);
        		connections.add(data);
    		}
    	}
    	//If -Z
    	if(!directionToIgnore.equals(ForgeDirection.NORTH)) {
    		byte tempByte = isElectricalUnit(world, x, y, z-1);
    		if(tempByte != 0) {
        		int[] position = {x, y, z-1, tempByte};
        		data = new ArrayList();
    			data.add(position);
        		data.add(ForgeDirection.SOUTH);
        		testString += "-SOUTH (Z:" + z + ") (NewZ:" + ( ((int[]) data.get(0))[2] ) + ")";
        		connections.add(data);
    		}
    	}
    	//If +Z
    	if(!directionToIgnore.equals(ForgeDirection.SOUTH)) {
    		byte tempByte = isElectricalUnit(world, x, y, z+1);
    		if(tempByte != 0) {
        		int[] position = {x, y, z+1, tempByte};
        		data = new ArrayList();
    			data.add(position);
        		data.add(ForgeDirection.NORTH);
        		testString += "-NORTH (Z:" + z + ") (NewZ:" + ( ((int[]) data.get(0))[2] ) + ")";
        		connections.add(data);
    		}
    	}
    	//If -Y
    	if(!directionToIgnore.equals(ForgeDirection.DOWN)) {
    		byte tempByte = isElectricalUnit(world, x, y-1, z);
    		if(tempByte != 0) {
        		int[] position = {x, y-1, z, tempByte};
        		data = new ArrayList();
    			data.add(position);
        		data.add(ForgeDirection.UP);
        		connections.add(data);
    		}
    	}
    	//If +Y
    	if(!directionToIgnore.equals(ForgeDirection.UP)) {
    		byte tempByte = isElectricalUnit(world, x, y+1, z);
    		if(tempByte != 0) {
        		int[] position = {x, y+1, z, tempByte};
        		data = new ArrayList();
    			data.add(position);
        		data.add(ForgeDirection.DOWN);
        		connections.add(data);
    		}
    	}
    	return connections;
    }
    
    /**
     * 	Checks whether the block at the specified location is either an Electrical Unit or an Electrical Medium
     * 	@param world	The world object for block access.
     * 	@param x		The X value
     * 	@param y		The y value
     * 	@param z		The z value
     * 	@return			A byte that says what the block is, set in the constants at the top of the class.
     */
    public static byte isElectricalUnit(World world, int x, int y, int z) {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if(block instanceof IElectricityWiring)
        	return EU_TYPE_WIRING;
        if(block instanceof IElectricityProducer)
        	return EU_TYPE_PRODUCER;
        if(block instanceof IElectricityConsumer)
        	return EU_TYPE_CONSUMER;
        if(block instanceof IElectricityAccumulator)
        	return EU_TYPE_ACCUMULATOR;
        return 0;
    }
    
    /**
     * 	Method used by electricity senders to determine valid locations.
     * 	@param world	The world object for block access.
     * 	@param x		The X value of the electricity sender.
     * 	@param y		The Y value of the electricity sender.
     * 	@param z		The Z value of the electricity sender.
     * 	@return			An ArrayList containing the locations of valid energy receivers.
     */
    public static ArrayList<int[]> getEnergyDestinations(World world, int x, int y, int z) {
    	//Get startpoint
    	ArrayList <ArrayList>connections = getConnections(world, x, y, z, ForgeDirection.UNKNOWN);
    	ArrayList <ArrayList>tempConnections;// = new ArrayList();
    	ArrayList energyDestinations = new ArrayList();	
    	int[] connectionPos;
    	
    	while(connections.size() > 0) {
    		tempConnections = new ArrayList();
    		
    		for(int i = 0; i<connections.size(); i++) {
    			connectionPos = (int[]) connections.get(i).get(0);
    			if(connectionPos[3] == EU_TYPE_CONSUMER || connectionPos[3] == EU_TYPE_ACCUMULATOR) {
    				energyDestinations.add(connectionPos);
    			} else {
    				tempConnections.addAll(getConnections(world, connectionPos[0], connectionPos[1], connectionPos[2], (ForgeDirection)connections.get(i).get(1)));
    			}
    		}

    		
    		if(tempConnections.size() > 0)
    			connections = tempConnections;
    		else
    			connections.clear();
    	}	
    	return energyDestinations;
    }

}
