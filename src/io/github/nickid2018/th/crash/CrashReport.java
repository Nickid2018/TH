package io.github.nickid2018.th.crash;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import oshi.SystemInfo;
import oshi.hardware.*;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CrashReport {

    private final List<CrashReportSession> sessions = Lists.newArrayList();
    private final String detail;
    private Throwable throwable;
    private String generatedReport;

    public CrashReport(String detail, Throwable error) {
        this.detail = detail;
        throwable = error;
    }

    public Throwable getCause(){
        return throwable;
    }

    public void setCause(Throwable cause){
        throwable = cause;
    }

    public static Stream<String> getVmArguments() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getInputArguments().stream().filter(string -> string.startsWith("-X"));
    }

    public static Stream<String> getUserArguments() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getInputArguments().stream().filter(string -> !string.startsWith("-X"));
    }

    private static <T> T getOrNull(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }

    public String populateReport() {
        if (generatedReport != null)
            return generatedReport;
        fillSystemDetails();
        StringWriter s = new StringWriter();
        PrintWriter writer = new PrintWriter(s);
        writer.println(" ----- TH Crash Report");
        writer.println("Time: " + (new SimpleDateFormat()).format(new Date()));
        writer.println("Description: " + detail);
        writer.println("Stack Trace:");
        throwable.printStackTrace(writer);
        writer.println();
        writer.println(" ----- The details are as follows: ");
        for (CrashReportSession session : sessions) {
            writer.println();
            writer.println(session.toString());
        }
        writer.println();
        writer.println(
                "// REM: If you want to use the crash report to report a bug, please delete all of private information!");
        return generatedReport = s.toString();
    }

    public void writeToFile() {
        try {
            File file = new File("crash-report/crash-"
                    + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
            File dir = file.getParentFile();
            if (!dir.isDirectory())
                dir.mkdirs();
            FileWriter fw;
            fw = new FileWriter(file);
            IOUtils.write(populateReport(), fw);
            fw.close();
        } catch (IOException e) {
            System.err.println("Can't save crash report!");
            e.printStackTrace();
        }
    }

    public void addSession(CrashReportSession session) {
        sessions.add(session);
    }

    public void addSession(CrashReportSession session, int pos) {
        sessions.add(pos, session);
    }

    private void fillSystemDetails() {
        CrashReportSession system = new CrashReportSession("System & Runtime Details");
        sessions.add(system);
        system.addDetailObject("TH version", "1.0");
        system.addDetailSupplier("Operating System", () -> System.getProperty("os.name") + " (" + System.getProperty("os.arch")
                + ") version " + System.getProperty("os.version"));
        system.addDetailSupplier("Java Version",
                () -> System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        system.addDetailSupplier("Java VM Version", () -> System.getProperty("java.vm.name") + " ("
                + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        system.addDetailSupplier("Memory", () -> {
            Runtime runtime = Runtime.getRuntime();
            long l = runtime.maxMemory();
            long l1 = runtime.totalMemory();
            long l2 = runtime.freeMemory();
            long l3 = l / 1024L / 1024L;
            long l4 = l1 / 1024L / 1024L;
            long l5 = l2 / 1024L / 1024L;
            return l2 + " bytes (" + l5 + " MB) / " + l1 + " bytes (" + l4 + " MB) up to " + l + " bytes (" + l3
                    + " MB)";
        });
        system.addDetailObject("CPU Count", Runtime.getRuntime().availableProcessors());
        system.addDetailSupplier("VM Arguments", () -> {
            StringBuilder sb = new StringBuilder();
            List<String> vmArgs = getVmArguments().collect(Collectors.toList());
            sb.append("Total count ");
            sb.append(vmArgs.size());
            sb.append(";");
            vmArgs.forEach(s -> sb.append(" ").append(s));
            return sb.toString();
        });
        system.addDetailSupplier("User Arguments", () -> {
            StringBuilder sb = new StringBuilder();
            List<String> userArgs = getUserArguments().collect(Collectors.toList());
            sb.append("Total count ");
            sb.append(userArgs.size());
            sb.append(";");
            userArgs.forEach(s -> sb.append(" ").append(s));
            return sb.toString();
        });
        HardwareAbstractionLayer hardware = getOrNull(() -> new SystemInfo().getHardware());
        system.addDetailSupplier("Processor", () -> {
            if (hardware == null)
                return "Unknown";
            CentralProcessor processor = hardware.getProcessor();
            CentralProcessor.ProcessorIdentifier id = processor.getProcessorIdentifier();
            return id == null ? "Unknown" : String.format("Vendor=%s; Name=%s; Identifier=%s; Micro-architecture=%s;" +
                            " Frequency=%.2f GHz; Physical packages=%d; Physical CPUs=%d; Logical CPUs=%d"
                    , id.getVendor(), id.getName(), id.getIdentifier(), id.getMicroarchitecture(),
                    (float) id.getVendorFreq() / 1.0E9F,
                    processor.getPhysicalPackageCount(), processor.getPhysicalProcessorCount(), processor.getLogicalProcessorCount());
        });
        List<GraphicsCard> gcards = hardware == null ? Lists.newArrayList() : hardware.getGraphicsCards();
        for (int i = 0; i < gcards.size(); i++) {
            GraphicsCard card = gcards.get(i);
            system.addDetailSupplier("Graphics Card #" + i, () ->
                    card == null ? "Unknown" : String.format("Name=%s; Vendor=%s; VRAM=%.2f MB; DeviceId=%s; VersionInfo=%s",
                            card.getName(), card.getVendor(),
                            (float) card.getVRam() / 1048576.0F, card.getDeviceId(), card.getVersionInfo()));
        }
        GlobalMemory memory = hardware == null ? null : hardware.getMemory();
        List<PhysicalMemory> phymems = memory == null ? Lists.newArrayList() : memory.getPhysicalMemory();
        for (int i = 0; i < phymems.size(); i++) {
            PhysicalMemory mem = phymems.get(i);
            system.addDetailSupplier("Memory Slot #" + i, () ->
                    mem == null ? "Unknown" : String.format("Capacity=%.2f MB; ClockSpeed=%.2f GHz; Type=%s",
                            (float) mem.getCapacity() / 1048576.0F,
                            (float) mem.getClockSpeed() / 1.0E9F, mem.getMemoryType()));
        }
        system.addDetailSupplier("Virtual Memory", () -> {
            if (memory == null)
                return "Unknown";
            VirtualMemory mem = memory.getVirtualMemory();
            return String.format("Max=%.2f MB; Used=%.2f MB; Swap Total=%.2f MB; Swap Used=%.2f MB",
                    (float) mem.getVirtualMax() / 1048576.0F,
                    (float) mem.getVirtualInUse() / 1048576.0F,
                    (float) mem.getSwapTotal() / 1048576.0F,
                    (float) mem.getSwapUsed() / 1048576.0F);
        });
    }
}
