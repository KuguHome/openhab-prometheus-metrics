package com.kuguhome.openhab.prometheusmetrics.exposable;

import org.apache.commons.lang.reflect.FieldUtils;
import org.openhab.binding.zwave.internal.ZWaveActiveBinding;
import org.openhab.binding.zwave.internal.protocol.ZWaveController;
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

@Component(service = { ZWaveMetric.class, RESTExposable.class })
public class ZWaveMetric implements RESTExposable {

    private final Logger logger = LoggerFactory.getLogger(ZWaveMetric.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
    protected volatile ZWaveActiveBinding zWaveActiveBinding;

    private final static Gauge openhabZWaveSendQueueLength = Gauge
            .build("openhab_zwave_global_send_queue_length", "openHAB Z-Wave send queue length").register(restRegistry);

    @Override
    public void expose() {
        // Field f;
        // try {
        // f = zWaveActiveBinding.getClass().getDeclaredField("zController");
        // f.setAccessible(true);
        // ZWaveController zWaveController = (ZWaveController) f.get(zWaveActiveBinding);
        // {
        // Child child = new Child();
        // child.set(zWaveController.getSendQueueLength());
        // openhabZWaveSendQueueLength.setChild(child);
        // }
        // } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // } // NoSuchFieldException

        try {
            ZWaveController zWaveController = (ZWaveController) FieldUtils.readField(zWaveActiveBinding, "zController",
                    true);
            {
                Child child = new Child();
                child.set(zWaveController.getSendQueueLength());
                openhabZWaveSendQueueLength.setChild(child);
            }

        } catch (IllegalAccessException e) {
            logger.error(e.getCause().getMessage());
        }
    }

    /*
     * protected void setZWaveActiveBinding(ZWaveActiveBinding zWaveActiveBinding) {
     * this.zWaveActiveBinding = zWaveActiveBinding;
     * }
     * 
     * protected void unsetZWaveActiveBinding() {
     * this.zWaveActiveBinding = null;
     * }
     */

    @Activate
    protected void activate() {
        logger.info(ZWaveMetric.class.getName() + " activated.");
    }

    @Deactivate
    protected void deactivate() {
        logger.info(ZWaveMetric.class.getName() + " deactivated.");
    }
}
