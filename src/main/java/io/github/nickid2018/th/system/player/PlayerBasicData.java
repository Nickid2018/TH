package io.github.nickid2018.th.system.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PlayerBasicData(String name, float hitRadius, float grazeRadius, float itemRadius,
                              float normalSpeed, float slowSpeed, PlayerRenderData renderData) {

    public static final Codec<PlayerBasicData> CODEC = RecordCodecBuilder.create(app -> app.group(
            Codec.STRING.fieldOf("name").forGetter(PlayerBasicData::name),
            Codec.FLOAT.fieldOf("hit_sphere_radius").forGetter(PlayerBasicData::hitRadius),
            Codec.FLOAT.fieldOf("graze_sphere_radius").forGetter(PlayerBasicData::grazeRadius),
            Codec.FLOAT.fieldOf("item_sphere_radius").forGetter(PlayerBasicData::itemRadius),
            Codec.FLOAT.fieldOf("normal_speed").forGetter(PlayerBasicData::normalSpeed),
            Codec.FLOAT.fieldOf("slow_speed").forGetter(PlayerBasicData::slowSpeed),
            PlayerRenderData.CODEC.fieldOf("player_render").forGetter(PlayerBasicData::renderData)
    ).apply(app, PlayerBasicData::new));
}
