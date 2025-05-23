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
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(AveriumMod.MOD_ID, "textures/gui/inert_infuser_gui.png");

    // Updated UV coordinates and dimensions for FILLED parts from your texture sheet
    // Progress Arrow Rectangle:
    private static final int PROGRESS_RECT_U = 176;
    private static final int PROGRESS_RECT_V = 2;
    private static final int PROGRESS_RECT_WIDTH_ON_SHEET = 30;
    private static final int PROGRESS_RECT_HEIGHT_ON_SHEET = 4; // Based on (176,2) to (205,5)

    // Progress Arrow Triangle:
    private static final int PROGRESS_TRI_U = 206; // (209 - 4 + 1)
    private static final int PROGRESS_TRI_V = 0;   // (Tip Y=3, Height 7 => Top V = 3 - floor(7/2) = 0)
    private static final int PROGRESS_TRI_WIDTH_ON_SHEET = 4;
    private static final int PROGRESS_TRI_HEIGHT_ON_SHEET = 7;

    // Fuel Bar (Gas Cell Fill):
    private static final int FUEL_BAR_U = 176;
    private static final int FUEL_BAR_V = 8;
    private static final int FUEL_BAR_WIDTH_ON_SHEET = 14; // (189 - 176 + 1)
    private static final int FUEL_BAR_HEIGHT_ON_SHEET = 4;  // (11 - 8 + 1)


    public InertInfuserScreen(InertInfuserMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 6;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight); // Draw main GUI background

        // Render progress bar
        // On-screen positions: Rectangle (74,27), Triangle (104, 25)
        if(menu.isCrafting()) {
            int scaledProgress = menu.getScaledProgress(); // Value from 0 to InertInfuserMenu.PROGRESS_ARROW_TOTAL_SCALABLE_WIDTH (34)

            // Draw rectangle part of the arrow
            int rectProgressToShow = Math.min(scaledProgress, PROGRESS_RECT_WIDTH_ON_SHEET);
            if (rectProgressToShow > 0) {
                guiGraphics.blit(TEXTURE,
                        x + 74, y + 27,               // Screen position (top-left of rect)
                        PROGRESS_RECT_U, PROGRESS_RECT_V,  // Texture UV for filled rect
                        rectProgressToShow, PROGRESS_RECT_HEIGHT_ON_SHEET); // Width (scaled), Height
            }

            // Draw triangle part of the arrow if progress extends into it
            if (scaledProgress > PROGRESS_RECT_WIDTH_ON_SHEET) {
                int triProgressToShow = Math.min(scaledProgress - PROGRESS_RECT_WIDTH_ON_SHEET, PROGRESS_TRI_WIDTH_ON_SHEET);
                // Screen Y for triangle: y + 25 (to center 7px tall triangle against 4px tall rect at y+27)
                // Rect center Y = 27 + (4/2) = 29. Triangle top Y = 29 - (7/2) = 25.5. Let's use 25.
                guiGraphics.blit(TEXTURE,
                        x + 74 + PROGRESS_RECT_WIDTH_ON_SHEET, y + 25,
                        PROGRESS_TRI_U, PROGRESS_TRI_V,
                        triProgressToShow, PROGRESS_TRI_HEIGHT_ON_SHEET);
            }
        }

        // Render fuel bar (assuming horizontal fill from left to right)
        // On-screen: Top-left (97, 57), Width 14, Height 4.
        int scaledFuelWidth = menu.getScaledFuel(); // Value from 0 to InertInfuserMenu.FUEL_BAR_SCALABLE_WIDTH (14)
        if (scaledFuelWidth > 0) {
            guiGraphics.blit(TEXTURE,
                    x + 97, y + 57,       // Screen pos (top-left of fuel bar area)
                    FUEL_BAR_U, FUEL_BAR_V, // Texture UV of the filled bar part
                    scaledFuelWidth, FUEL_BAR_HEIGHT_ON_SHEET); // Scaled Width, Full Height
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
