package io.github.nickid2018.th.system;

import io.github.nickid2018.th.system.pack.PackManager;

public class ResourceManager {

    private final PackManager packManager;

    public ResourceManager(PackManager packManager) {
        this.packManager = packManager;
    }

    public PackManager getPackManager() {
        return packManager;
    }


}
