var PlaygroundClass = Java.type("io.github.nickid2018.th.system.Playground");
var ModBaseClass = Java.type("io.github.nickid2018.th.system.ScriptRunner");

/**
 * Call when player was hit by a bullet.
 *
 * @param playGround Playground object
 * @param bullet A bullet
 */
function hitOnPlayer(playGround, bullet) {
    playGround.hitOnPlayer(bullet);
}

function checkHit(playGround, item) {
    return playGround.getPlayer().getHitSphere().orthogonalWith(item.getHitSphere());
}

function modifyProperty(item, name, value) {
    item.properties().put(name, value);
}

function getProperty(item, name) {
    return item.properties().get(name);
}

function throwJavaException(exception) {
    ModBaseClass.exceptionFromJS(exception);
}

function cast(object, javaType) {
    return ModBaseClass.cast(object, javaType);
}
