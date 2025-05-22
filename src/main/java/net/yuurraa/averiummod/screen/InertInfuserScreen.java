// src/main/java/net/yuurraa/averiummod/screen/InertInfuserScreen.java
package net.yuurraa.averiummod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.yuurraa.averiummod.AveriumMod;

public class InertInfuserScreen extends AbstractContainerScreen<InertInfuserMenu> {
    // Define the location of your GUI texture
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(AveriumMod.MOD_ID, "textures/gui/inert_infuser_gui.png");

    public InertInfuserScreen(InertInfuserMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        // You can set a custom image height if your GUI texture is taller than the default (166)
        // this.imageHeight = 166; // Default
    }

    @Override
    protected void init() {
        super.init();
        // Called when the screen is opened. You can set initial positions here if needed.
        // this.titleLabelX = (imageWidth - font.width(title)) / 2; // Centers title if you want
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Draw the background GUI texture
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Render progress bar
        if(menu.isCrafting()) {
            // x, y, textureX, textureY, width, height
            // Assuming your progress arrow starts at (176, 0) in the texture file and is 24 wide, 17 high
            guiGraphics.blit(TEXTURE, x + 80, y + 35, 176, 0, menu.getScaledProgress(), 17);
        }

        // Render fuel bar
        // Assuming your fuel bar texture starts at (176, 17) in the texture file, is 14 wide and 13 high (when full)
        // And it fills upwards, so we adjust the y-coordinate and height
        int fuelHeight = menu.getScaledFuel(); // This is the scaled height (0 to 13)
        if (fuelHeight > 0) {
            guiGraphics.blit(TEXTURE, x + 62, y + 36 + (13 - fuelHeight), 176, 17 + (13 - fuelHeight), 14, fuelHeight);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        // Corrected line:
        this.renderBackground(guiGraphics); // This calls the method to render the default dimmed background

        super.render(guiGraphics, mouseX, mouseY, delta); // Renders slots, items, title, player inv label etc.
        renderTooltip(guiGraphics, mouseX, mouseY); // Renders item tooltips
    }

    // You can override renderLabels if you want to draw custom text, like energy levels,
    // but the title and player inventory labels are usually handled by the superclass's render method.
    // @Override
    // protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
    //     // super.renderLabels(pGuiGraphics, pMouseX, pMouseY); // Don't call super.renderLabels if you want to fully customize
    //     pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    //     pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    //     // Example: pGuiGraphics.drawString(this.font, "Energy: " + menu.getEnergy(), X, Y, 0x404040, false);
    // }
}
