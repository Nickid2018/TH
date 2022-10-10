package io.github.nickid2018.th.system;

import io.github.nickid2018.th.crash.CrashReport;
import io.github.nickid2018.th.crash.CrashReportSession;
import io.github.nickid2018.th.crash.DetectedCrashException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jdk.dynalink.beans.StaticClass;

import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class ScriptRunner {

    public static final Logger JS_LOGGER = LogManager.getLogger("Java Script Mod");

    public static ScriptRunner instance;

    public Set<String> loadedPackages = new HashSet<>();
    private ScriptEngine scriptEngine;
    private boolean valid = false;

    public ScriptRunner(){
        instance = this;
    }

    public void init() {
        engineInitialize();
        if(valid) {
//            if(!importSystemJS("system")){
//                valid = false;
//                JS_LOGGER.warn("(JS Engine) system.js cannot be loaded! JS Engine will be disabled.");
//            }
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void evalString(String eval) throws ScriptException {
        if(valid) {
            scriptEngine.getContext().setAttribute(ScriptEngine.FILENAME, null, ScriptContext.ENGINE_SCOPE);
            scriptEngine.eval(eval);
        } else
            JS_LOGGER.warn("(JS Engine) Code attempts to run but engine has been disabled.");
    }

    private void engineInitialize() {
        ScriptEngineManager manager = new ScriptEngineManager();
        scriptEngine = manager.getEngineByName("javascript");
        valid = scriptEngine != null;
        if(!valid)
            JS_LOGGER.warn("(JS Engine) Cannot initialize! JS Engine will be disabled.");
    }

    public void evalJS(InputStream source, String name) throws IOException, ScriptException {
        if(valid) {
            String info = IOUtils.toString(source, StandardCharsets.UTF_8);
            scriptEngine.getContext().setAttribute(ScriptEngine.FILENAME, name, ScriptContext.ENGINE_SCOPE);
            scriptEngine.eval(info);
        } else
            JS_LOGGER.warn("(JS Engine) Code attempts to run but engine has been disabled.");
    }

    public void terminate() {
        if(valid) {

        }
    }

    // --- JS APIs ---
    public static void exceptionFromJS(Throwable exception) throws Throwable {
        throw exception;
    }

    public static void crashFromJS(DetectedCrashException exception) {
        CrashReport report = exception.getReport();
        report.getCause().fillInStackTrace();
        CrashReportSession session = new CrashReportSession("JavaScript");
        report.addSession(session);
        session.addDetailObject("Engine", instance.scriptEngine);
        ScriptEngineFactory factory = instance.scriptEngine.getFactory();
        session.addDetailObject("Engine Name", factory.getEngineName());
        session.addDetailObject("Engine Version" , factory.getEngineVersion());
        session.addDetailObject("Language Name", factory.getLanguageName());
        session.addDetailObject("Language Version", factory.getLanguageVersion());
        throw exception;
    }

    /**
     * system.js - cast
     * @param object a JavaScript-Mirrored object
     * @param castClass the class target to cast
     * @param <T> class target
     * @return a Java object
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object object, StaticClass castClass) {
        if(object instanceof ScriptObjectMirror mirror)
            return (T) mirror.to(castClass.getRepresentedClass());
        else
            return (T) castClass.getRepresentedClass().cast(object);
    }
}