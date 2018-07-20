package com.kuguhome.openhab.prometheusmetrics.exposable;

import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kuguhome.openhab.prometheusmetrics.api.RESTExposable;
import com.kuguhome.openhab.prometheusmetrics.rest.PrometheusMetricsRESTResource;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.Gauge.Child;

@Component(service = { RESTExposable.class })
public class OpenHABBundleStateMetric implements RESTExposable {

    private final Logger logger = LoggerFactory.getLogger(OpenHABBundleStateMetric.class);

    private final static Gauge openhabBundleState = Gauge.build("openhab_bundle_state", "openHAB OSGi bundles state")
            .labelNames("bundle").register(CollectorRegistry.defaultRegistry);

    @Override
    public void expose() {
        Bundle[] bundles = FrameworkUtil.getBundle(PrometheusMetricsRESTResource.class).getBundleContext().getBundles();
        Stream.of(bundles).parallel().forEach(b -> {
            Child child = new Child();
            child.set(b.getState());
            openhabBundleState.setChild(child, b.getSymbolicName());
        });
    }

    @Activate
    protected void activate() {
        logger.info(OpenHABBundleStateMetric.class.getName() + " activated.");
    }

    @Deactivate
    protected void deactivate() {
        logger.info(OpenHABBundleStateMetric.class.getName() + " deactivated.");
    }

}
