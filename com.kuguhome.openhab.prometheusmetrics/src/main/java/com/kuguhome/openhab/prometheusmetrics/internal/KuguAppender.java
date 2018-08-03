package com.kuguhome.openhab.prometheusmetrics.internal;

import static org.apache.logging.log4j.Level.*;

import java.util.Dictionary;
import java.util.Hashtable;

import org.ops4j.pax.logging.PaxLoggingService;
import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import io.prometheus.client.Counter;

@Component(service = { PaxAppender.class })
public class KuguAppender implements PaxAppender {

    @Activate
    protected void activate(BundleContext context) {
        System.out.println("KuguAppender appender activated.");
        final Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(PaxLoggingService.APPENDER_NAME_PROPERTY, "Kugu");
        ServiceRegistration m_appenderRegistration = context.registerService(PaxAppender.class.getName(), this,
                properties);
    }

    @Override
    public void doAppend(PaxLoggingEvent event) {

        System.out.println("KuguAppender: " + event.getMessage());

        if (TRACE.intLevel() == event.getLevel().toInt()) {
            TRACE_LABEL.inc();
        } else if (DEBUG.intLevel() == event.getLevel().toInt()) {
            DEBUG_LABEL.inc();
        } else if (INFO.intLevel() == event.getLevel().toInt()) {
            INFO_LABEL.inc();
        } else if (WARN.intLevel() == event.getLevel().toInt()) {
            WARN_LABEL.inc();
        } else if (ERROR.intLevel() == event.getLevel().toInt()) {
            ERROR_LABEL.inc();
        } else if (FATAL.intLevel() == event.getLevel().toInt()) {
            FATAL_LABEL.inc();
        } else {
            System.out.println("Level: " + event.getLevel().toInt());
        }
    }

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

}
