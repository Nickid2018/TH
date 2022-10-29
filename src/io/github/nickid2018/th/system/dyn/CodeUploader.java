package io.github.nickid2018.th.system.dyn;

public class CodeUploader extends ClassLoader {

    public Class<?> defineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }
}
