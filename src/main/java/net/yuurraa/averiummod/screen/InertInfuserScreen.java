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

    // Progress Arrow UVs and Dims (remain the same)
    private static final int PROGRESS_RECT_U = 176; //
    private static final int PROGRESS_RECT_V = 2; //
    private static final int PROGRESS_RECT_WIDTH_ON_SHEET = 30; //
    private static final int PROGRESS_RECT_HEIGHT_ON_SHEET = 4; //

    private static final int PROGRESS_TRI_U = 206; //
    private static final int PROGRESS_TRI_V = 0; //
    private static final int PROGRESS_TRI_WIDTH_ON_SHEET = 4; //
    private static final int PROGRESS_TRI_HEIGHT_ON_SHEET = 7; //

    // Fuel Bar (Gas Cell Fill) UVs and Dims
    private static final int FUEL_BAR_U = 176; //
    // V for Xenon (current/default)
    private static final int FUEL_BAR_XENON_V = 8; // This was the original FUEL_BAR_V
    // V for Argon (Xenon V + 6)
    private static final int FUEL_BAR_ARGON_V = FUEL_BAR_XENON_V + 6; // 8 + 6 = 14

    private static final int FUEL_BAR_WIDTH_ON_SHEET = 14; //
    private static final int FUEL_BAR_HEIGHT_ON_SHEET = 4;  //


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

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Render progress bar (using previously corrected X coordinates)
        if(menu.isCrafting()) {
            int scaledProgress = menu.getScaledProgress();
            int rectProgressToShow = Math.min(scaledProgress, PROGRESS_RECT_WIDTH_ON_SHEET);
            if (rectProgressToShow > 0) {
                guiGraphics.blit(TEXTURE,
                        x + 63, y + 27, // Screen position
                        PROGRESS_RECT_U, PROGRESS_RECT_V,
                        rectProgressToShow, PROGRESS_RECT_HEIGHT_ON_SHEET);
            }
            if (scaledProgress > PROGRESS_RECT_WIDTH_ON_SHEET) {
                int triProgressToShow = Math.min(scaledProgress - PROGRESS_RECT_WIDTH_ON_SHEET, PROGRESS_TRI_WIDTH_ON_SHEET);
                guiGraphics.blit(TEXTURE,
                        x + 63 + PROGRESS_RECT_WIDTH_ON_SHEET, y + 25, // Screen position
                        PROGRESS_TRI_U, PROGRESS_TRI_V,
                        triProgressToShow, PROGRESS_TRI_HEIGHT_ON_SHEET);
            }
        }

        // Render fuel bar (using previously corrected X coordinate: x + 86)
        int scaledFuelWidth = menu.getScaledFuel();
        if (scaledFuelWidth > 0) {
            int activeGasRenderType = menu.getActiveGasRenderType();
            int fuelBarTextureV;

            if (activeGasRenderType == 2) { // Argon
                fuelBarTextureV = FUEL_BAR_ARGON_V;
            } else if (activeGasRenderType == 1) { // Xenon (or default if type is 0 but fuel is somehow > 0)
                fuelBarTextureV = FUEL_BAR_XENON_V;
            } else {
                // Default or fallback if activeGasRenderType is 0 (no gas) but scaledFuelWidth > 0
                // This case should ideally not happen if logic is correct,
                // but good to have a default. Could also choose not to render.
                fuelBarTextureV = FUEL_BAR_XENON_V; // Or some "empty but active" texture if you had one
                if (activeGasRenderType == 0) return; // Optionally, don't render if gas type is 0
            }

            // Only render if we have a valid gas type to show
            if (activeGasRenderType != 0) { // Or check scaledFuelWidth > 0 && activeGasRenderType != 0
                guiGraphics.blit(TEXTURE,
                        x + 86, y + 57,       // Screen pos (top-left of fuel bar area)
                        FUEL_BAR_U, fuelBarTextureV, // Use the CHOSEN V coordinate
                        scaledFuelWidth, FUEL_BAR_HEIGHT_ON_SHEET);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}