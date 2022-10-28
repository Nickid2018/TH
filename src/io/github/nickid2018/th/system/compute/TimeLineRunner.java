package io.github.nickid2018.th.system.compute;

public abstract class TimeLineRunner implements Tickable {

    protected final Playground playground;

    public TimeLineRunner(Playground playground) {
        this.playground = playground;
    }
}
