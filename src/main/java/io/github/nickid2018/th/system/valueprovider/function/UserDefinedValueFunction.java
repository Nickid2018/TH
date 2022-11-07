package io.github.nickid2018.th.system.valueprovider.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.nickid2018.th.system.compute.HittableItem;
import io.github.nickid2018.th.system.script.ScriptRunner;
import io.github.nickid2018.th.system.valueprovider.ValueProvider;
import io.github.nickid2018.th.util.ResourceLocation;
import lombok.Getter;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

import java.util.List;

public class UserDefinedValueFunction<T> extends ValueFunction<T> {

    public static final Codec<UserDefinedValueFunction<?>> CODEC = RecordCodecBuilder.create(app -> app.group(
            ResourceLocation.CODEC.fieldOf("location").forGetter(UserDefinedValueFunction::getLocation),
            Codec.STRING.xmap(ValueProvider::nameToClass, ValueProvider::classToName).listOf()
                    .fieldOf("args").forGetter(u -> List.of(u.arguments))
    ).apply(app, UserDefinedValueFunction::new));
    @Getter
    private final ResourceLocation location;
    private final ScriptObjectMirror object;
    private final ScriptFunction function;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public UserDefinedValueFunction(ResourceLocation location,
                                    List<Class> arguments) {
        super(location.toString(), arguments.toArray(Class[]::new));
        this.location = location;
        object = ScriptRunner.loadAndCreateObjectMirror(location, "valueCompute");
        function = ScriptRunner.getScriptFunction(location, object);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T getValue(HittableItem item, List<ValueProvider<?>> arguments) {
        try {
            if (object == null || function == null)
                return null;
            return (T) ScriptRunner.runScriptNoInvalidate(object, function, item, arguments);
        } catch (Exception e) {
            ScriptRunner.JS_LOGGER.error("Failed to run Value Function script " + location, e);
            return null;
        }
    }
}
