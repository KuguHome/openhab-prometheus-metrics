package com.kuguhome.openhab.prometheusmetrics.internal;

import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

//@Component(service = { KuguAppender.class, LogService.class, PaxAppender.class })
public class KuguAppender implements PaxAppender, LogService {

    @Override
    public void doAppend(PaxLoggingEvent e) {

        System.out.println("Event: " + e.getMessage());

    }

    @Override
    public void log(int arg0, String arg1) {
        System.out.println("");

    }

    @Override
    public void log(int arg0, String arg1, Throwable arg2) {
        System.out.println("");

    }

    @Override
    public void log(ServiceReference arg0, int arg1, String arg2) {
        System.out.println("");

    }

    @Override
    public void log(ServiceReference arg0, int arg1, String arg2, Throwable arg3) {
        System.out.println("");

    }

}
