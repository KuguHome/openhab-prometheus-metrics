/**
 * Copyright (c) 2010-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.kuguhome.openhab.prometheusmetrics.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.ops4j.pax.logging.PaxLoggingService;
import org.ops4j.pax.logging.spi.PaxAppender;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link PrometheusMetricsActivator}
 *
 * @author Roman Malyugin
 */

public final class PrometheusMetricsActivator implements BundleActivator {

    private static Logger logger = LoggerFactory.getLogger(PrometheusMetricsActivator.class);

    private static BundleContext context;

    /**
     * Called whenever the OSGi framework starts our bundle
     */
    @Override
    public void start(BundleContext bc) throws Exception {
        context = bc;
        logger.debug("PrometheusMetrics action has been started.");

        // LogServiceImpl logService = new LogServiceImpl(configur1ationAdmin, size);
        // Hashtable<String, Object> props = new Hashtable<>();
        // props.put("org.ops4j.pax.logging.appender.name", "VmLogAppender");
        // register(KuguAppender.class, logService, props);
        // register(LogService.class, logService);
        final PaxAppender appender = new KuguAppender();
        final Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(PaxLoggingService.APPENDER_NAME_PROPERTY, "Kugu");
        ServiceRegistration m_appenderRegistration = context.registerService(PaxAppender.class.getName(), appender,
                properties);
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    @Override
    public void stop(BundleContext bc) throws Exception {
        context = null;
        logger.debug("PrometheusMetrics action has been stopped.");
    }

    /**
     * Returns the bundle context of this bundle
     *
     * @return the bundle context
     */
    public static BundleContext getContext() {
        return context;
    }

}
