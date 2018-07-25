package com.kuguhome.openhab.prometheusmetrics.exposable;

import io.prometheus.client.hotspot.BufferPoolsExports;
import io.prometheus.client.hotspot.ClassLoadingExports;
import io.prometheus.client.hotspot.GarbageCollectorExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.hotspot.ThreadExports;
import io.prometheus.client.hotspot.VersionInfoExports;

public class JVMMetric {

    static ThreadExports threadExports;

    private static boolean initialized = false;

    public static synchronized void initialize() {
        if (!initialized) {
            new StandardExports().register();
            new MemoryPoolsExports().register();
            new BufferPoolsExports().register();
            new GarbageCollectorExports().register();
            threadExports = new ThreadExports().register();

            new ClassLoadingExports().register();
            new VersionInfoExports().register();
            initialized = true;
        }
    }

}
