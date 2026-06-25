package dev.galiano.neko;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record OnekoConfig(float scale, int idleInterval, int alertDistance, int speed) {
    //todo keep in sync with src/main/resources/assets/neko/neko_config.json
    public static final OnekoConfig DEFAULT = new OnekoConfig(1f, 30, 32, 10);

    public static final Codec<OnekoConfig> CODEC = RecordCodecBuilder.create(
        i -> i.group(
            Codec.floatRange(0f, 2f).optionalFieldOf("scale", DEFAULT.scale).forGetter(OnekoConfig::scale),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("idle_interval", DEFAULT.idleInterval).forGetter(OnekoConfig::idleInterval),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("alert_distance", DEFAULT.alertDistance).forGetter(OnekoConfig::alertDistance),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("speed", DEFAULT.speed).forGetter(OnekoConfig::speed)
        ).apply(i, OnekoConfig::new)
    );
}
