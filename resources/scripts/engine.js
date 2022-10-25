var ScriptRunner = Java.type('io.github.nickid2018.th.system.script.ScriptRunner');
var JS_LOGGER = ScriptRunner.JS_LOGGER;

function checkPackage(name) {
    if (!ScriptRunner.instance.packageLoaded(name)) {
        try {
            ScriptRunner.instance.loadPackage(name);
        } catch (e) {
            error('Failed to load package ' + name, e);
        }
        return false;
    }
    return true;
}

function print(msg) {
    JS_LOGGER.info(msg);
}

function error(msg, e) {
    JS_LOGGER.error(msg, e);
}