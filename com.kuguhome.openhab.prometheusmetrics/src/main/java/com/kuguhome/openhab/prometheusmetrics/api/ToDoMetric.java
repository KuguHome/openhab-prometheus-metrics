package com.kuguhome.openhab.prometheusmetrics.api;

import java.util.concurrent.CountDownLatch;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.Gauge;
import io.prometheus.client.Gauge.Child;

/**
 * This class describes the example TODO Metric
 *
 * @author Roman Malyugin
 *
 */

@Component(service = { ToDoMetric.class, RESTExposable.class })
public class ToDoMetric implements RESTExposable {

    private final Logger logger = LoggerFactory.getLogger(ToDoMetric.class);

    private CountDownLatch countDownLatch = new CountDownLatch(50);

    private final static Gauge openhabToDoCount = Gauge.build("todo_count_down", "openHAB TODO count down")
            .register(restRegistry);

    @Override
    public void expose() {
        {
            Child child = new Child();
            child.set(countDownLatch.getCount());
            openhabToDoCount.setChild(child);
        }
        countDownLatch.countDown();
    }

    @Activate
    protected void activate() {
        logger.info(ToDoMetric.class.getName() + " activated.");
    }

    @Deactivate
    protected void deactivate() {
        logger.info(ToDoMetric.class.getName() + " deactivated.");
    }

}
