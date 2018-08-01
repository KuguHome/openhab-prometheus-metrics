package com.kuguhome.openhab.prometheusmetrics.exposable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kuguhome.openhab.prometheusmetrics.internal.PrometheusMetricsActivator;

import io.prometheus.client.hotspot.BufferPoolsExports;
import io.prometheus.client.hotspot.ClassLoadingExports;
import io.prometheus.client.hotspot.GarbageCollectorExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.hotspot.ThreadExports;
import io.prometheus.client.hotspot.VersionInfoExports;

public class JVMMetric {
    private static final Logger logger = LogManager.getLogger(PrometheusMetricsActivator.class);

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
