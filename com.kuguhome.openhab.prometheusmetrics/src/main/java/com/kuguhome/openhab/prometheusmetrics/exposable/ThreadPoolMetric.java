package com.kuguhome.openhab.prometheusmetrics.exposable;

import java.util.concurrent.ThreadPoolExecutor;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kuguhome.openhab.prometheusmetrics.api.RESTExposable;
import com.kuguhome.openhab.prometheusmetrics.internal.KuguThreadPoolManager;

import io.prometheus.client.Gauge;
import io.prometheus.client.Gauge.Child;

/**
 * This class describes the example ThreadPool Metric
 *
 * @author Roman Malyugin
 *
 */

@Component(service = { ThreadPoolMetric.class, RESTExposable.class })
public class ThreadPoolMetric implements RESTExposable {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolMetric.class);

    private final static Gauge openhabThreadPoolCount = Gauge
            .build("openhab_active_threads_count", "openHAB threadpools counter").labelNames("pool")
            .register(restRegistry);
    private final static Gauge openhabThreadPoolSize = Gauge
            .build("openhab_thread_pool_size", "openHAB threadpools size").labelNames("pool").register(restRegistry);
    private final static Gauge openhabMaximunThreadPoolSize = Gauge
            .build("openhab_maximun_thread_pool_size", "openHAB Maximun threadpools size").labelNames("pool")
            .register(restRegistry);
    private final static Gauge openhabThreadPoolCompletedTastCount = Gauge
            .build("openhab_completed_task_count", "openHAB threadpools completed task counter").labelNames("pool")
            .register(restRegistry);
    private final static Gauge openhabThreadPoolTastCount = Gauge
            .build("openhab_task_count", "openHAB threadpools task counter").labelNames("pool").register(restRegistry);
    private final static Gauge openhabLargestThreadPoolSize = Gauge
            .build("openhab_largest_thread_pool_size", "openHAB largest threadpools size").labelNames("pool")
            .register(restRegistry);

    protected KuguThreadPoolManager threadPoolManager;

    @Override
    public void expose() {
        threadPoolManager.getPools().forEach((n, es) -> {
            if (es instanceof ThreadPoolExecutor) {
                ThreadPoolExecutor tpe = (ThreadPoolExecutor) es;
                {
                    Child child = new Child();
                    child.set(tpe.getActiveCount());
                    openhabThreadPoolCount.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getPoolSize());
                    openhabThreadPoolSize.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getCompletedTaskCount());
                    openhabThreadPoolCompletedTastCount.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getLargestPoolSize());
                    openhabLargestThreadPoolSize.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getTaskCount());
                    openhabThreadPoolTastCount.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getMaximumPoolSize());
                    openhabMaximunThreadPoolSize.setChild(child, n);
                }
            }
        });
    }

    @Activate
    protected void activate() {
        logger.info(ThreadPoolMetric.class.getName() + " activated.");
    }

    @Deactivate
    protected void deactivate() {
        logger.info(ThreadPoolMetric.class.getName() + " deactivated.");
    }

    public void unsetThreadPoolManager() {
        this.threadPoolManager = null;
    }

    @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.DYNAMIC)
    public void setThreadPoolManager(KuguThreadPoolManager threadPoolManager) {
        this.threadPoolManager = threadPoolManager;
    }

}
