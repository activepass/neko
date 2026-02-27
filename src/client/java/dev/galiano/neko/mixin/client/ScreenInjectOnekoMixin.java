package dev.galiano.neko.mixin.client;

import dev.galiano.neko.Oneko;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenInjectOnekoMixin {
	@Inject(at = @At("TAIL"), method = "render")
	private void renderNeko(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
		if (!((Screen)(Object)this instanceof AbstractContainerScreen)) Oneko.render(graphics, mouseX, mouseY, delta);
	}
}