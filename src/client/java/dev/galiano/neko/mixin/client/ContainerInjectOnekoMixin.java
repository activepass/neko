package dev.galiano.neko.mixin.client;

import dev.galiano.neko.Oneko;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerInjectOnekoMixin {
    @Inject(at = @At("TAIL"), method = "extractContents")
    private void renderNeko(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
        Oneko.render(graphics, mouseX, mouseY, delta);
    }
}
