package com.kuguhome.openhab.prometheusmetrics.exposable;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.inbox.Inbox;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.kuguhome.openhab.prometheusmetrics.api.RESTExposable;
import com.kuguhome.openhab.prometheusmetrics.internal.PrometheusMetricsActivator;

import io.prometheus.client.Gauge;
import io.prometheus.client.Gauge.Child;

/**
 * This class describes the Inbox Count Metric
 *
 * @author Roman Malyugin
 *
 */

@Component(service = { InboxCountMetric.class, RESTExposable.class })
public class InboxCountMetric implements RESTExposable {

    private static final Logger logger = LogManager.getLogger(PrometheusMetricsActivator.class);

    protected Inbox inbox;

    private final static Gauge openhabInboxCount = Gauge.build("openhab_inbox_count", "openHAB inbox count")
            .register(restRegistry);

    @Override
    public void expose() {
        List<DiscoveryResult> inboxList = inbox.getAll();
        {
            Child child = new Child();
            child.set(inboxList.size());
            openhabInboxCount.setChild(child);
        }

    }

    @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
    protected void setInbox(Inbox inbox) {
        this.inbox = inbox;
    }

    protected void unsetInbox(Inbox inbox) {
        this.inbox = null;
    }

    @Activate
    protected void activate() {
        logger.info(InboxCountMetric.class.getName() + " activated.");
    }

    @Deactivate
    protected void deactivate() {
        logger.info(InboxCountMetric.class.getName() + " deactivated.");
    }

}
