package org.jelledevos.minefactory.gui;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.StatCollector;

import org.jelledevos.minefactory.container.ContainerAccumulator;
import org.jelledevos.minefactory.container.ContainerFossilFuelPS;
import org.jelledevos.minefactory.tileentity.TileEntityAccumulator;
import org.jelledevos.minefactory.tileentity.TileEntityFossilFuelPS;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiAccumulator extends GuiContainer {

    private TileEntityAccumulator accumulatorInventory;

    public GuiAccumulator(InventoryPlayer inventoryPlayer, TileEntityAccumulator entityAccumulator) {
        super(new ContainerAccumulator(inventoryPlayer, entityAccumulator));
        this.accumulatorInventory = entityAccumulator;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer() {
        this.fontRenderer.drawString("Energy Accumulator", 27, 6, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        int textureInt = this.mc.renderEngine.getTexture("/gui/MineFactory/accumulator.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(textureInt);
        int guiWidth = (this.width - this.xSize) / 2;
        int guiHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(guiWidth, guiHeight, 0, 0, this.xSize, this.ySize);
        
        //Energy Indicator
        int energyIndicatorHeight = 52;
        int currentStorageScaled = accumulatorInventory.getCurrentStoragePercentageScaledTo(energyIndicatorHeight);
        int energyIndicatorStart = energyIndicatorHeight - currentStorageScaled;
        drawTexturedModalRect(guiWidth + 44, guiHeight + 17 + energyIndicatorStart, 176, 0, 16, currentStorageScaled);
        
        
//        int burningRemainingScaled;
//        if (this.accumulatorInventory.isBurning()) {
//            burningRemainingScaled = this.accumulatorInventory.getBurnTimeRemainingScaled(12);
//            this.drawTexturedModalRect(guiWidth + 56, guiHeight + 36 + 12 - burningRemainingScaled, 176, 12 - burningRemainingScaled, 14, burningRemainingScaled + 2);
//        }

//        burningRemainingScaled = this.powerStationInventory.getCookProgressScaled(24);
//        this.drawTexturedModalRect(guiWidth + 79, guiHeight + 34, 176, 14, burningRemainingScaled + 1, 16);
    }
}
