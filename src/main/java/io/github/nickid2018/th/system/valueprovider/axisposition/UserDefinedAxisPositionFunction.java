package io.github.nickid2018.th.system.valueprovider.axisposition;

import io.github.nickid2018.th.system.script.ScriptRunner;
import io.github.nickid2018.th.system.valueprovider.IntToFloatFunction;
import io.github.nickid2018.th.util.ResourceLocation;
import lombok.Getter;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

@Getter
public class UserDefinedAxisPositionFunction implements AxisPositionFunction {

    private final ResourceLocation location;
    private final ScriptObjectMirror object;
    private final ScriptFunction function;

    public UserDefinedAxisPositionFunction(ResourceLocation location) {
        this.location = location;
        object = ScriptRunner.loadAndCreateObjectMirror(location, "axisCompute");
        function = ScriptRunner.getScriptFunction(location, object);
    }


    @Override
    public float getValue(float t, IntToFloatFunction arguments) {
        try {
            if (object == null || function == null)
                return 0;
            return ((Number) ScriptRunner.runScriptNoInvalidate(object, function, t, arguments)).floatValue();
        } catch (Exception e) {
            ScriptRunner.JS_LOGGER.error("Failed to run Axis Position script " + location, e);
            return 0;
        }
    }
}
