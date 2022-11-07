package io.github.nickid2018.th.crash;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Supplier;

public class CrashReportSession {

    private final String name;
    private final Thread thread;
    private final Map<String, Supplier<String>> details = Maps.newLinkedHashMap();

    public CrashReportSession(String name) {
        this.name = name;
        thread = Thread.currentThread();
    }

    public CrashReportSession addDetailSupplier(String name, Supplier<String> info) {
        details.put(name, info);
        return this;
    }

    public CrashReportSession addDetailObject(String name, Object obj) {
        return addDetailSupplier(name, () -> {
            if (obj == null)
                return "[null]";
            if (obj instanceof Throwable)
                return "[ERROR]" + obj.getClass() + ":" + ((Throwable) obj).getLocalizedMessage();
            return obj.toString();
        });
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("  --- " + name + " ---");
        sb.append("\r\n");
        sb.append("\tThread: ");
        sb.append(thread.getName());
        sb.append("\r\n");
        for (Map.Entry<String, Supplier<String>> entry : details.entrySet()) {
            sb.append("\t");
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue().get());
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
