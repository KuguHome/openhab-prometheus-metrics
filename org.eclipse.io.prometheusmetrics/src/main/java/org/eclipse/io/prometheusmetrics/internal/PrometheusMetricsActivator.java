package org.eclipse.io.prometheusmetrics.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PrometheusMetricsActivator implements BundleActivator {

    private static Logger logger = LoggerFactory.getLogger(PrometheusMetricsActivator.class);

    /**
     * Called whenever the OSGi framework starts our bundle
     */
    @Override
    public void start(BundleContext bc) throws Exception {
        logger.debug("PrometheusMetrics binding has been started.");
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    @Override
    public void stop(BundleContext bc) throws Exception {
        logger.debug("PrometheusMetrics binding has been stopped.");
    }

}
