package com.kuguhome.openhab.prometheusmetrics.exposable;

import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kuguhome.openhab.prometheusmetrics.api.RESTExposable;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.Gauge.Child;

@Component(service = { RESTExposable.class })
public class OpenHABThingStateMetric implements RESTExposable {

    private final Logger logger = LoggerFactory.getLogger(OpenHABThingStateMetric.class);

    private ThingRegistry thingRegistry;

    private final static Gauge openhabThingState = Gauge.build("openhab_thing_state", "openHAB Things state")
            .labelNames("thing").register(CollectorRegistry.defaultRegistry);

    @Override
    public void expose() {
        thingRegistry.getAll().parallelStream().forEach(t -> {
            Child child = new Child();
            child.set(t.getStatus().ordinal());
            openhabThingState.setChild(child, t.getUID().getAsString());
        });
    }

    @Reference
    protected void setThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = thingRegistry;
    }

    protected void unsetThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = null;
    }

    @Activate
    protected void activate() {
        logger.info(OpenHABThingStateMetric.class.getName() + " activated.");
    }

    @Deactivate
    protected void deactivate() {
        logger.info(OpenHABThingStateMetric.class.getName() + " deactivated.");
    }

}
