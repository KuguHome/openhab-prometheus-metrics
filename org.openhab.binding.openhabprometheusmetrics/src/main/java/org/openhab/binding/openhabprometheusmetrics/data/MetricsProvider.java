/**
 * Copyright (c) 2015-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.openhabprometheusmetrics.data;

import java.util.stream.Stream;

/**
 * The base interface for a metrics provider
 *
 * @author Roman Malyugin - Initial contribution
 *
 */
public interface MetricsProvider<T extends MetricItem> {
    /**
     * Gets a stream of {@link MetricItem} metrics items
     *
     * @return stream of metrics
     * @throws Exception
     */
    Stream<MetricItem> getMetrics() throws Exception;

}
