package io.github.nickid2018.th.pack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.util.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public record PackDataList(Optional<Map<String, ResourceLocation>> playerData,
                           Optional<Map<String, ResourceLocation>> stageData,
                           Optional<Map<String, ResourceLocation>> bulletData,
                           Optional<Map<String, ResourceLocation>> functionData) {

    public static final Codec<PackDataList> CODEC = RecordCodecBuilder.create(app -> app.group(
            Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC).optionalFieldOf("player_data").forGetter(PackDataList::playerData),
            Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC).optionalFieldOf("stage_data").forGetter(PackDataList::stageData),
            Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC).optionalFieldOf("bullet_data").forGetter(PackDataList::bulletData),
            Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC).optionalFieldOf("function_data").forGetter(PackDataList::functionData)
    ).apply(app, PackDataList::new));
}
