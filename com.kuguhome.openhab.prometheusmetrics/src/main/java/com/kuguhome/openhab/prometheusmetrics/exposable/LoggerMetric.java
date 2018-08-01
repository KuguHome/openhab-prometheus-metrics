package com.kuguhome.openhab.prometheusmetrics.exposable;

import org.osgi.service.component.annotations.Component;

import com.kuguhome.openhab.prometheusmetrics.api.RESTExposable;

/**
 * This class describes the Logger Metric
 *
 * @author Roman Malyugin
 *
 */

@Component(service = { LoggerMetric.class, RESTExposable.class })
public class LoggerMetric implements RESTExposable {

    @Override
    public void expose() {
        // TODO Auto-generated method stub

    }

}
