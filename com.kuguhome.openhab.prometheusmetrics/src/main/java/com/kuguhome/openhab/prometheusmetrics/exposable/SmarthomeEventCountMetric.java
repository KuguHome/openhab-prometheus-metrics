package com.kuguhome.openhab.prometheusmetrics.exposable;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.events.EventFilter;
import org.eclipse.smarthome.core.events.EventSubscriber;
import org.eclipse.smarthome.core.items.events.ItemCommandEvent;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.kuguhome.openhab.prometheusmetrics.api.RESTExposable;
import com.kuguhome.openhab.prometheusmetrics.internal.PrometheusMetricsActivator;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.Gauge.Child;

/**
 * This class describes the Smarthome Event Count Metric
 * Events only of smarthome topic
 *
 * @author Roman Malyugin
 *
 */

@Component(service = { SmarthomeEventCountMetric.class, RESTExposable.class, EventSubscriber.class })
public class SmarthomeEventCountMetric implements RESTExposable, EventSubscriber {

    private static final Logger logger = LogManager.getLogger(PrometheusMetricsActivator.class);

    private final static Gauge smarthomeEventCount = Gauge.build("smarthome_event_count", "openHAB event count")
            .labelNames("source").register(CollectorRegistry.defaultRegistry);

    private Map<String, Queue<WeakReference<org.eclipse.smarthome.core.events.@NonNull Event>>> smarthomeEventCache = new ConcurrentHashMap<>();

    @Override
    public void expose() {
        smarthomeEventCache.forEach((s, q) -> {
            Child child = new Child();
            child.set(q.size());
            smarthomeEventCount.setChild(child, s);
        });
    }

    @Activate
    protected void activate() {
        logger.info(SmarthomeEventCountMetric.class.getName() + " activated.");
    }

    @Deactivate
    protected void deactivate() {
        logger.info(SmarthomeEventCountMetric.class.getName() + " deactivated.");
    }

    @Override
    public void receive(org.eclipse.smarthome.core.events.@NonNull Event event) {
        if (event.getTopic().startsWith("smarthome/")) {
            String source = event.getSource();
            if (source == null) {
                logger.debug("Event source equals to null.");
                String[] tmp = event.getTopic().split("/");
                source = tmp[tmp.length - 2];
            }
            smarthomeEventCache.putIfAbsent(source, new ConcurrentLinkedQueue<>());
            WeakReference<org.eclipse.smarthome.core.events.@NonNull Event> weakReference = new WeakReference<>(event);
            smarthomeEventCache.get(source).add(weakReference);
        } else {
            logger.debug("Received event not from smarthome");
        }
    }

    @Override
    public Set<String> getSubscribedEventTypes() {
        Set<String> types = new HashSet<>(2);
        types.add(ItemCommandEvent.TYPE);
        types.add(ItemStateEvent.TYPE);
        return types;
    }

    @Override
    public EventFilter getEventFilter() {
        return null;
    }

}
