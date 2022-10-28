var bridge = {

    getClass: function (name) {
        return Java.type(name);
    },

    getStaticElement: function (clazz, name) {
        return Java.type(clazz)[name];
    }
};

var engine = {

    ScriptRunner: bridge.getClass('io.github.nickid2018.th.system.script.ScriptRunner'),
    JS_LOGGER: bridge.getStaticElement('io.github.nickid2018.th.system.script.ScriptRunner', 'JS_LOGGER'),
    packageLoaded: bridge.getStaticElement('io.github.nickid2018.th.system.script.ScriptRunner', 'packageLoaded'),
    loadPackage: bridge.getStaticElement('io.github.nickid2018.th.system.script.ScriptRunner', 'loadPackage'),

    checkPackage: function (name) {
        if (!engine.packageLoaded(name)) {
            try {
                engine.loadPackage(name);
            } catch (e) {
                error('Failed to load package ' + name, e);
            }
            return false;
        }
        return true;
    },

    print: function (msg) {
        engine.JS_LOGGER.info(msg);
    },

    error: function (msg, e) {
        engine.JS_LOGGER.error(msg, e);
    }
};

function print(msg) {
    engine.print(msg);
}

function error(msg, e) {
    engine.error(msg, e);
}