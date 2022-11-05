package io.github.nickid2018.th.system.valueprovider.axisposition;

import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.system.script.ScriptRunner;
import io.github.nickid2018.th.system.valueprovider.IntToFloatFunction;
import io.github.nickid2018.th.util.ResourceLocation;
import lombok.Getter;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

import java.util.Objects;

@Getter
public class UserDefinedAxisPositionFunction implements AxisPositionFunction {

    private final ResourceLocation location;
    private final ScriptObjectMirror object;
    private final ScriptFunction function;

    public UserDefinedAxisPositionFunction(ResourceLocation location) {
        this.location = location;
        ScriptObjectMirror mirror;
        ScriptFunction scriptFunction;
        try {
            if (!ScriptRunner.packageLoaded(location))
                ScriptRunner.loadPackage(location);
            String name = Objects.requireNonNull(
                    PackManager.getPack(PackManager.getNamespaceDefaultSelect(location.path()))
            ).getScriptVariableName(location.path());
            mirror = (ScriptObjectMirror) ScriptRunner.getJSObject(name).getMember("axisCompute");
        } catch (Exception e) {
            ScriptRunner.JS_LOGGER.error("Failed to load script " + location, e);
            mirror = null;
        }
        object = mirror;
        if (object != null)
            try {
                scriptFunction = (ScriptFunction) ScriptRunner.SOBJ_FIELD.get(mirror);
            } catch (Exception e) {
                ScriptRunner.JS_LOGGER.error("Failed to load script " + location, e);
                scriptFunction = null;
            }
        else
            scriptFunction = null;
        function = scriptFunction;
    }


    @Override
    public float getValue(float t, IntToFloatFunction arguments) {
        try {
            if (object == null)
                return 0;
            if (function == null)
                return 0;
            return ((Number) ScriptRunner.runScriptNoInvalidate(object, function, t, arguments)).floatValue();
        } catch (Exception e) {
            ScriptRunner.JS_LOGGER.error("Failed to run Axis Position script " + location, e);
            return 0;
        }
    }
}
