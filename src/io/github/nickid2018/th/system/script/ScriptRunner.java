package io.github.nickid2018.th.system.script;

import io.github.nickid2018.th.crash.CrashReport;
import io.github.nickid2018.th.crash.CrashReportSession;
import io.github.nickid2018.th.crash.DetectedCrashError;
import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ECMAException;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("unchecked")
public class ScriptRunner {

    public static final Logger JS_LOGGER = LogManager.getLogger("Java Script Mod");
    private static ScriptEngine scriptEngine;

    private static final Set<ResourceLocation> loadedScripts = new HashSet<>();


    public static final Field SOBJ_FIELD;
    private static final Field GLOBAL;
    private static final ThreadLocal<Global> CURRENT_GLOBAL;
    private static final Method INVOKE_METHOD;

    static {
        try {
            SOBJ_FIELD = ScriptObjectMirror.class.getDeclaredField("sobj");
            SOBJ_FIELD.setAccessible(true);
            Field currentGlobal = Context.class.getDeclaredField("currentGlobal");
            currentGlobal.setAccessible(true);
            CURRENT_GLOBAL = (ThreadLocal<Global>) currentGlobal.get(null);
            GLOBAL = ScriptObjectMirror.class.getDeclaredField("global");
            GLOBAL.setAccessible(true);
            INVOKE_METHOD = ScriptFunction.class.getDeclaredMethod("invoke", Object.class, Object[].class);
            INVOKE_METHOD.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        scriptEngine = new ScriptEngineManager().getEngineByName("javascript");

        try {
            loadPackage(ResourceLocation.internal("scripts/engine.js"));
            loadPackage(ResourceLocation.internal("scripts/primitive_types.js"));
        } catch (ScriptException | IOException e) {
            JS_LOGGER.error("Failed to load engine script", e);
        }
    }

    public static void evalString(String eval) throws ScriptException {
        scriptEngine.getContext().setAttribute(ScriptEngine.FILENAME, null, ScriptContext.ENGINE_SCOPE);
        scriptEngine.eval(eval);
    }

    public static boolean packageNotLoaded(ResourceLocation location) {
        return !loadedScripts.contains(location);
    }

    public static void loadPackage(ResourceLocation location) throws ScriptException, IOException {
        loadPackage(PackManager.createInputStream(location), location.normalize());
    }

    public static void loadPackage(String info, ResourceLocation location) throws ScriptException {
        scriptEngine.getContext().setAttribute(ScriptEngine.FILENAME, location.toString(), ScriptContext.ENGINE_SCOPE);
        scriptEngine.eval(info);
        loadedScripts.add(location);
    }

    public static void loadPackage(InputStream source, ResourceLocation name) throws IOException, ScriptException {
        loadPackage(IOUtils.toString(source, StandardCharsets.UTF_8), name);
    }

    public static JSObject getJSObject(String name) {
        return (JSObject) scriptEngine.get(name);
    }

    public static ScriptObjectMirror loadAndCreateObjectMirror(ResourceLocation location, String name) {
        try {
            if (packageNotLoaded(location))
                loadPackage(location);
            String objectName = Objects.requireNonNull(
                    PackManager.getPack(location.namespace())).getScriptVariableName(location.path());
            return (ScriptObjectMirror) getJSObject(objectName).getMember(name);
        } catch (Exception e) {
            JS_LOGGER.error("Failed to load script " + location, e);
            return null;
        }
    }

    public static ScriptFunction getScriptFunction(ResourceLocation location, ScriptObjectMirror mirror) {
        if (mirror != null)
            try {
                return (ScriptFunction) SOBJ_FIELD.get(mirror);
            } catch (Exception e) {
                JS_LOGGER.error("Failed to load script " + location, e);
            }
        return null;
    }

    public static Object runScriptNoInvalidate(ScriptObjectMirror object,
                                               ScriptFunction function,
                                               Object... args) throws Exception {
        try {
            CURRENT_GLOBAL.set((Global) GLOBAL.get(object));
            return INVOKE_METHOD.invoke(function, null, args);
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            if (target instanceof ECMAException || target instanceof Error) {
                CrashReport report = new CrashReport("Script Error", target);
                CrashReportSession session = new CrashReportSession("Script Engine");
                session.addDetailObject("Script Engine", scriptEngine);
                session.addDetailObject("Script Object", object.getClassName());
                session.addDetailObject("Script Function", function);
                session.addDetailObject("Script Arguments", Arrays.toString(args));
                report.addSession(session);
                throw new DetectedCrashError(report);
            } else
                throw (Exception) target;
        }
    }
}