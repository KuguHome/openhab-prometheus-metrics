package com.kuguhome.openhab.prometheusmetrics.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.ops4j.pax.logging.PaxLoggingService;
import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import io.prometheus.client.Counter;

@Component(service = { PaxAppender.class })
public class KuguAppender implements PaxAppender {

    @Activate
    protected void activate(BundleContext context) {
        final Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(PaxLoggingService.APPENDER_NAME_PROPERTY, "Kugu");
        context.registerService(PaxAppender.class.getName(), this, properties);
    }

    @Override
    public void doAppend(PaxLoggingEvent event) {
        switch (event.getLevel().toString()) {
            case "TRACE":
                TRACE_LABEL.inc();
                break;
            case "DEBUG":
                DEBUG_LABEL.inc();
                break;
            case "WARN":
                WARN_LABEL.inc();
                break;
            case "ERROR":
                ERROR_LABEL.inc();
                break;
            case "INFO":
                INFO_LABEL.inc();
                break;
            case "FATAL":
                FATAL_LABEL.inc();
                break;
        }
    }

    public static final String COUNTER_NAME = "openhab_logmessage_count";

    private static final Counter COUNTER;
    private static final Counter.Child TRACE_LABEL;
    private static final Counter.Child DEBUG_LABEL;
    private static final Counter.Child INFO_LABEL;
    private static final Counter.Child WARN_LABEL;
    private static final Counter.Child ERROR_LABEL;
    private static final Counter.Child FATAL_LABEL;

    static {
        COUNTER = Counter.build().name(COUNTER_NAME).help("logmessage count at various log levels").labelNames("level")
                .register();

        TRACE_LABEL = COUNTER.labels("trace");
        DEBUG_LABEL = COUNTER.labels("debug");
        INFO_LABEL = COUNTER.labels("info");
        WARN_LABEL = COUNTER.labels("warn");
        ERROR_LABEL = COUNTER.labels("error");
        FATAL_LABEL = COUNTER.labels("fatal");
    }

}
