package io.github.nickid2018.th.system.dyn;

public class CodeUploader extends ClassLoader {

    public static final String FIELD_SCRIPT_OBJECT = "scriptObject";
    public static final String FIELD_SCRIPT_FUNCTION = "scriptFunction";
    public static final String FIELD_TYPE_SCRIPT_OBJECT = "Lorg/openjdk/nashorn/api/scripting/ScriptObjectMirror;";
    public static final String FIELD_TYPE_SCRIPT_FUNCTION = "Lorg/openjdk/nashorn/internal/runtime/ScriptFunction;";
    public static final String METHOD_TICK = "tick";
    public static final String METHOD_TICK_DESC = "(J)V";
    public static final String CTOR = "<init>";
    public static final String CLINIT = "<clinit>";
    public static final String CLINIT_DESC = "()V";
    public static final String RESOURCE_LOCATION = "io/github/nickid2018/th/util/ResourceLocation";
    public static final String RESOURCE_LOCATION_FROM_STRING = "fromString";
    public static final String RESOURCE_LOCATION_FROM_STRING_DESC =
            "(Ljava/lang/String;)Lio/github/nickid2018/th/util/ResourceLocation;";
    public static final String SCRIPT_RUNNER = "io/github/nickid2018/th/system/script/ScriptRunner";
    public static final String SCRIPT_RUNNER_CREATE_OBJECT = "loadAndCreateObjectMirror";
    public static final String SCRIPT_RUNNER_CREATE_OBJECT_DESC =
            "(Lio/github/nickid2018/th/util/ResourceLocation;Ljava/lang/String;)Lorg/openjdk/nashorn/api/scripting/ScriptObjectMirror;";
    public static final String SCRIPT_RUNNER_CREATE_FUNCTION = "getScriptFunction";
    public static final String SCRIPT_RUNNER_CREATE_FUNCTION_DESC =
            "(Lio/github/nickid2018/th/util/ResourceLocation;Lorg/openjdk/nashorn/api/scripting/ScriptObjectMirror;)" +
                    "Lorg/openjdk/nashorn/internal/runtime/ScriptFunction;";
    public static final String SCRIPT_RUNNER_RUN_SCRIPT = "runScriptNoInvalidate";
    public static final String SCRIPT_RUNNER_RUN_SCRIPT_DESC =
            "(Lorg/openjdk/nashorn/api/scripting/ScriptObjectMirror;Lorg/openjdk/nashorn/internal/runtime/ScriptFunction;[Ljava/lang/Object;)" +
                    "Ljava/lang/Object;";
    public static final String JS_LOGGER = "JS_LOGGER";
    public static final String JS_LOGGER_DESC = "Lorg/sl4j/Logger;";


    public static final CodeUploader INSTANCE = new CodeUploader();


    public Class<?> defineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }
}
