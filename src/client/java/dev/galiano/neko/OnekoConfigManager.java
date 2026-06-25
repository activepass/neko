package dev.galiano.neko;

import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.Reader;

public class OnekoConfigManager extends SimplePreparableReloadListener<OnekoConfig> {
    private static final Identifier CONFIG_LOCATION = Identifier.fromNamespaceAndPath("neko", "neko_config.json");

    public OnekoConfig instance = OnekoConfig.DEFAULT;

    @Override
    protected OnekoConfig prepare(final ResourceManager manager, final ProfilerFiller profiler) {
        try (Reader reader = manager.openAsReader(CONFIG_LOCATION)) {
            return OnekoConfig.CODEC.parse(JsonOps.INSTANCE, StrictJsonParser.parse(reader)).getOrThrow();
        } catch (Exception e) {
            Oneko.LOGGER.error("Could not parse neko/neko_config.json", e);
            return OnekoConfig.DEFAULT;
        }
    }

    @Override
    protected void apply(final OnekoConfig preparations, final ResourceManager manager, final ProfilerFiller profiler) {
        this.instance = preparations;
    }
}
