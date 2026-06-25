package dev.galiano.neko;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Oneko implements ClientModInitializer {
	public static Logger LOGGER = LoggerFactory.getLogger("neko");
	public static Identifier NEKO_TEXTURE = Identifier.fromNamespaceAndPath("neko", "textures/gui/oneko.png");
	static final int SPRITE_SIZE = 32;
	static final int SPRITE_CENTRE = SPRITE_SIZE / 2;

	static int posX = SPRITE_SIZE, posY = SPRITE_SIZE;
	static int spriteX = 3, spriteY = 3; // start idle

	public static final OnekoConfigManager manager = new OnekoConfigManager();

	@Override
	public void onInitializeClient() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES)
			.registerReloadListener(Identifier.fromNamespaceAndPath("neko", "data_manager"), manager);
	}

	static long lastFrame = 0;
	static long frameTimer = 0;
	static long idleTimer = 0;
	static int idleAnimFrame = 0;
	static IdleAnimation idleAnimation = IdleAnimation.IDLE;

	enum IdleAnimation {
		IDLE,
		SLEEPING,
		SCRATCH,
		SCRATCH_N,
		SCRATCH_S,
		SCRATCH_E,
		SCRATCH_W;

		private static final int size = IdleAnimation.values().length;
		public static final ArrayList<IdleAnimation> AVAILABLE = new ArrayList<>(size); // available will never be full so this should be fine
	}

	static void setSprite(int x, int y) {
		spriteX = x;
		spriteY = y;
	}

	static void resetIdle() {
		idleAnimation = IdleAnimation.IDLE;
		idleAnimFrame = 0;
	}

	static void idle() {
		idleTimer++;

		if (idleTimer > 10 && Math.floor(Math.random() * manager.instance.idleInterval()) == 0 && idleAnimation == IdleAnimation.IDLE) {
			IdleAnimation.AVAILABLE.clear();
			IdleAnimation.AVAILABLE.add(IdleAnimation.SCRATCH);
			IdleAnimation.AVAILABLE.add(IdleAnimation.SLEEPING);

			var bound = Math.round(SPRITE_SIZE * manager.instance.scale());
			var window = Minecraft.getInstance().getWindow();
			if (posX < bound) IdleAnimation.AVAILABLE.add(IdleAnimation.SCRATCH_W);
			if (posY < bound) IdleAnimation.AVAILABLE.add(IdleAnimation.SCRATCH_N);
			if (posX > window.getGuiScaledWidth() - bound) IdleAnimation.AVAILABLE.add(IdleAnimation.SCRATCH_E);
			if (posY > window.getGuiScaledHeight() - bound) IdleAnimation.AVAILABLE.add(IdleAnimation.SCRATCH_S);
			idleAnimation = IdleAnimation.AVAILABLE.get(ThreadLocalRandom.current().nextInt(IdleAnimation.AVAILABLE.size()));
		}

		var a = idleAnimFrame % 2;
		switch (idleAnimation) {
			case SLEEPING -> {
				if (idleAnimFrame < 8) {
					setSprite(3, 2); // tired
					break;
				}
				setSprite(2, (int)Math.floor(idleAnimFrame / 4f) % 2); // [2, 0] [2, 1]
				if (idleAnimFrame > 192) resetIdle();
			}
			// this sucks but i cant be asked
			case SCRATCH_N -> {
				setSprite(0, a); // [0, 0] [0, 1]
				if (idleAnimFrame > 9) resetIdle();
			}
			case SCRATCH_S -> {
				setSprite(7-a, 1+a); // [7, 1] [6, 2]
				if (idleAnimFrame > 9) resetIdle();
			}
			case SCRATCH_E -> {
				setSprite(2, 2+a); // [2, 2] [2, 3]
				if (idleAnimFrame > 9) resetIdle();
			}
			case SCRATCH_W -> {
				setSprite(4, a); // [4, 0] [4, 1]
				if (idleAnimFrame > 9) resetIdle();
			}
			case SCRATCH -> {
				setSprite(5 + (idleAnimFrame % 3), 0);
				if (idleAnimFrame > 9) resetIdle();
			}
			default -> {
				setSprite(3, 3); // idle
				return;
			}
		}

		idleAnimFrame++;
	}

	static void update_neko(int mouseX, int mouseY) {
		frameTimer++;
		var diffX = posX - mouseX;
		var diffY = posY - mouseY;
		var distance = Math.sqrt((diffX * diffX) + (diffY * diffY));
		if (distance < manager.instance.alertDistance() * manager.instance.scale()) {
			idle();
			return;
		}
		resetIdle();

		if (idleTimer > 1) {
			setSprite(7, 3); // alert
			idleTimer = Math.min(idleTimer, 7);
			idleTimer--;
			return;
		}

		// i didnt want to do the string map stuff and this ended up worse
		var mask = 0b0000;
		if (diffY / distance > 0.5) mask |=  0b1000; // N
		if (diffY / distance < -0.5) mask |= 0b0100; // S
		if (diffX / distance > 0.5) mask |=  0b0010; // W
		if (diffX / distance < -0.5) mask |= 0b0001; // E

		int b = (int) frameTimer % 2;
		var a = b == 0;
		switch (mask) {
            case 0b1000 -> setSprite(1, 2 + b);      // N  [1, 2] [1, 3]
			case 0b0100 -> setSprite(6 + b, 3 - b);  // S  [6, 3] [7, 2]
			case 0b0010 -> setSprite(4, 2 + b);      // W  [4, 2] [4, 3]
			case 0b0001 -> setSprite(3, b);             // E  [3, 0] [3, 1]
			case 0b1001 -> setSprite(0, 2 + b);      // NE [0, 2] [0, 3]
			case 0b0101 -> setSprite(5, 1 + b);      // SE [5, 1] [5, 2]
			case 0b0110 -> setSprite(5 + b, a ? 3 : 1); // SW [5, 3] [6, 1]
			case 0b1010 -> setSprite(1, b);             // NW [1, 0] [1, 1]
        };

		posX -= (int) ((diffX / distance) * manager.instance.speed() * manager.instance.scale());
		posY -= (int) ((diffY / distance) * manager.instance.speed() * manager.instance.scale());

		var bound = Math.round(SPRITE_CENTRE * manager.instance.scale());
		posX = Math.min(Math.max(bound, posX), Minecraft.getInstance().getWindow().getGuiScaledWidth() - bound);
		posY = Math.min(Math.max(bound, posY), Minecraft.getInstance().getWindow().getGuiScaledHeight() - bound);
	}

	public static void render(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float delta) {
		if (Util.getMillis() - lastFrame > 100) {
			lastFrame = Util.getMillis();
			update_neko(mouseX, mouseY);
		}

		extractor.pose().pushMatrix();
		extractor.pose().translate(-SPRITE_CENTRE * manager.instance.scale(), -SPRITE_CENTRE * manager.instance.scale());
		extractor.blit(
			RenderPipelines.GUI_TEXTURED,
			NEKO_TEXTURE,
			posX,
			posY,
			SPRITE_SIZE * spriteX,
			SPRITE_SIZE * spriteY,
			Math.round(SPRITE_SIZE * manager.instance.scale()),
			Math.round(SPRITE_SIZE * manager.instance.scale()),
			SPRITE_SIZE,
			SPRITE_SIZE,
			256,
			128
		);
		// extractor.fill(posX-1, posY-1, posX+1, posY+1, 0xFF00FF00);
		extractor.pose().popMatrix();
		// extractor.fill(posX-1, posY-1, posX+1, posY+1, 0xFFFF0000);
		// var scaled_alert_dist = Math.round(ALERT_DISTANCE * SCALE);
		// extractor.fill(posX-scaled_alert_dist, posY-scaled_alert_dist, posX+scaled_alert_dist, posY+scaled_alert_dist, 0x22AAAA00);
	}
}