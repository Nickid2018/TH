package io.github.nickid2018.th.system.script;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.nashorn.api.scripting.JSObject;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class ScriptRunner {

    public static final Logger JS_LOGGER = LogManager.getLogger("Java Script Mod");
    private static ScriptEngine scriptEngine;

    private static final Set<String> loadedScripts = new HashSet<>();

    public static void init() {
        scriptEngine = new ScriptEngineManager().getEngineByName("javascript");

        // Load engine script
        try {
            loadPackage(ScriptRunner.class.getResourceAsStream("/scripts/engine.js"), "<engine_internal>");
        } catch (ScriptException | IOException e) {
            JS_LOGGER.error("Failed to load engine script", e);
        }
    }

    public static boolean packageLoaded(String name) {
        return loadedScripts.contains(name);
    }

    public static void evalString(String eval) throws ScriptException {
        scriptEngine.getContext().setAttribute(ScriptEngine.FILENAME, null, ScriptContext.ENGINE_SCOPE);
        scriptEngine.eval(eval);
    }

    public static void loadPackage(String name) throws ScriptException, IOException {
        loadPackage(ScriptRunner.class.getResourceAsStream(name), name);
    }

    public static void loadPackage(String info, String name) throws ScriptException {
        scriptEngine.getContext().setAttribute(ScriptEngine.FILENAME, name, ScriptContext.ENGINE_SCOPE);
        scriptEngine.eval(info);
        loadedScripts.add(name);
    }

    public static void loadPackage(InputStream source, String name) throws IOException, ScriptException {
        String info = IOUtils.toString(source, StandardCharsets.UTF_8);
        loadPackage(info, name);
    }

    public static JSObject getJSObject(String name) {
        return (JSObject) scriptEngine.get(name);
    }

    public static void main(String[] args) {
        ScriptRunner.init();
        try {
            ScriptRunner.evalString("""
                    print(engine.packageLoaded);
                    """);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}