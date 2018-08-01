package com.kuguhome.openhab.prometheusmetrics.internal;

import static org.apache.logging.log4j.Level.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import io.prometheus.client.Counter;

@Plugin(name = "Prometheus", category = "Core", elementType = "appender")
public class KuguAppender extends AbstractAppender {

    public static final String COUNTER_NAME = "log4j2_appender_total";

    private static final Counter COUNTER;
    private static final Counter.Child TRACE_LABEL;
    private static final Counter.Child DEBUG_LABEL;
    private static final Counter.Child INFO_LABEL;
    private static final Counter.Child WARN_LABEL;
    private static final Counter.Child ERROR_LABEL;
    private static final Counter.Child FATAL_LABEL;

    static {
        COUNTER = Counter.build().name(COUNTER_NAME).help("Log4j2 log statements at various log levels")
                .labelNames("level").register();

        TRACE_LABEL = COUNTER.labels("trace");
        DEBUG_LABEL = COUNTER.labels("debug");
        INFO_LABEL = COUNTER.labels("info");
        WARN_LABEL = COUNTER.labels("warn");
        ERROR_LABEL = COUNTER.labels("error");
        FATAL_LABEL = COUNTER.labels("fatal");
    }

    /**
     * Create a new instrumented appender using the default registry.
     */
    protected KuguAppender(String name) {
        super(name, null, null);
    }

    @Override
    public void append(LogEvent event) {
        Level level = event.getLevel();
        if (TRACE.equals(level)) {
            TRACE_LABEL.inc();
        } else if (DEBUG.equals(level)) {
            DEBUG_LABEL.inc();
        } else if (INFO.equals(level)) {
            INFO_LABEL.inc();
        } else if (WARN.equals(level)) {
            WARN_LABEL.inc();
        } else if (ERROR.equals(level)) {
            ERROR_LABEL.inc();
        } else if (FATAL.equals(level)) {
            FATAL_LABEL.inc();
        }
    }

    @PluginFactory
    public static KuguAppender createAppender(@PluginAttribute("name") String name) {
        if (name == null) {
            LOGGER.error("No name provided for InstrumentedAppender");
            return null;
        }
        return new KuguAppender(name);
    }

}
