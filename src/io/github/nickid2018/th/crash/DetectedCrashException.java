package io.github.nickid2018.th.crash;

import java.io.Serial;

public class DetectedCrashException extends RuntimeException {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -1430051357808450887L;

    private final CrashReport report;

    public DetectedCrashException(CrashReport report) {
        this.report = report;
    }

    public CrashReport getReport() {
        return report;
    }
}
