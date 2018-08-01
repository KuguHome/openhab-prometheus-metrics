package com.kuguhome.openhab.prometheusmetrics.exposable;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
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

    private final static Gauge openhabActiveThreadsCount = Gauge
            .build("openhab_pool_threads_count_active",
                    "approximate number of threads that are actively executing tasks")
            .labelNames("pool").register(restRegistry);
    private final static Gauge openhabThreadPoolSize = Gauge
            .build("openhab_pool_threads_count_current", "current number of threads in the pool").labelNames("pool")
            .register(restRegistry);
    private final static Gauge openhabThreadPoolCompletedTaskCount = Gauge
            .build("openhab_pool_tasks_count_completed",
                    "approximate total number of tasks that have completed execution")
            .labelNames("pool").register(restRegistry);
    private final static Gauge openhabLargestThreadPoolSize = Gauge
            .build("openhab_pool_threads_count_largest",
                    "largest number of threads that have ever simultaneously been in the pool")
            .labelNames("pool").register(restRegistry);
    private final static Gauge openhabMaximunThreadPoolSize = Gauge
            .build("openhab_pool_size_max", "maximum allowed number of threads in the pool").labelNames("pool")
            .register(restRegistry);
    private final static Gauge openhabThreadPoolTaskCount = Gauge
            .build("openhab_pool_tasks_count_total",
                    "total number of tasks that have ever been scheduled for execution")
            .labelNames("pool").register(restRegistry);
    private final static Gauge openhabCorePoolSize = Gauge
            .build("openhab_pool_size", "regular number of threads in the pool").labelNames("pool")
            .register(restRegistry);
    private final static Gauge openhabThreadPoolQueueSize = Gauge
            .build("openhab_pool_queue_count", "the number of tasks waiting to get executed by a thread")
            .labelNames("pool").register(restRegistry);
    private final static Gauge openhabKeepAliveTime = Gauge.build("openhab_pool_keepalive_time_seconds",
            "the amount of time that threads in excess of the regular pool size may remain idle before being terminated")
            .labelNames("pool").register(restRegistry);

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected KuguThreadPoolManager threadPoolManager;

    @Override
    public void expose() {
        threadPoolManager.getPools().forEach((n, es) -> {
            if (es instanceof ThreadPoolExecutor) {
                ThreadPoolExecutor tpe = (ThreadPoolExecutor) es;
                {
                    Child child = new Child();
                    child.set(tpe.getActiveCount());
                    openhabActiveThreadsCount.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getPoolSize());
                    openhabThreadPoolSize.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getCompletedTaskCount());
                    openhabThreadPoolCompletedTaskCount.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getLargestPoolSize());
                    openhabLargestThreadPoolSize.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getTaskCount());
                    openhabThreadPoolTaskCount.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getMaximumPoolSize());
                    openhabMaximunThreadPoolSize.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getCorePoolSize());
                    openhabCorePoolSize.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getQueue().size());
                    openhabThreadPoolQueueSize.setChild(child, n);
                }
                {
                    Child child = new Child();
                    child.set(tpe.getKeepAliveTime(TimeUnit.SECONDS));
                    openhabKeepAliveTime.setChild(child, n);
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

}
