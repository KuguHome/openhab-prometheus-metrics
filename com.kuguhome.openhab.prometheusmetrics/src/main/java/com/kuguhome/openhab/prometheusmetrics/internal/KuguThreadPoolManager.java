package com.kuguhome.openhab.prometheusmetrics.internal;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.smarthome.core.common.ThreadPoolManager;
import org.osgi.service.component.annotations.Component;

@Component(service = { KuguThreadPoolManager.class, ThreadPoolManager.class })
public class KuguThreadPoolManager extends ThreadPoolManager {

    private static final Logger logger = LogManager.getLogger(KuguThreadPoolManager.class);

    public Map<String, ExecutorService> getPools() {
        return pools;
    }

}
