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
        ScriptObjectMirror tmp;
        try {
            if (!ScriptRunner.packageLoaded(location))
                ScriptRunner.loadPackage(location);
            String name = Objects.requireNonNull(
                    PackManager.getPack(PackManager.getNamespaceDefaultSelect(location.path()))
            ).getScriptVariableName(location.path());
            tmp = (ScriptObjectMirror) ScriptRunner.getJSObject(name).getMember("axisCompute");
        } catch (Exception e) {
            ScriptRunner.JS_LOGGER.error("Failed to load script " + location, e);
            tmp = null;
        }
        object = tmp;
        try {
            function = (ScriptFunction) ScriptRunner.SOBJ_FIELD.get(tmp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public float getValue(float t, IntToFloatFunction arguments) {
        try {

            return ((Number) ScriptRunner.runScriptNoInvalidate(object, function, t, arguments)).floatValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
