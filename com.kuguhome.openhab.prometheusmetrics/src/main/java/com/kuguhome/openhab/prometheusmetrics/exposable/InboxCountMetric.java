package com.kuguhome.openhab.prometheusmetrics.exposable;

import java.util.List;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.inbox.Inbox;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kuguhome.openhab.prometheusmetrics.api.RESTExposable;

import io.prometheus.client.Gauge;
import io.prometheus.client.Gauge.Child;

@Component(service = { RESTExposable.class })
public class InboxCountMetric implements RESTExposable {

    private final Logger logger = LoggerFactory.getLogger(InboxCountMetric.class);

    private Inbox inbox;
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

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
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
