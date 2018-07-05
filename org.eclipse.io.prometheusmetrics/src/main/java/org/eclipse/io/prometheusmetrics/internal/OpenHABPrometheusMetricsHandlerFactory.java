/**
 * Copyright (c) 2018,2018 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.io.prometheusmetrics.internal;

import static org.eclipse.io.prometheusmetrics.internal.OpenHABPrometheusMetricsBindingConstants.*;

import java.util.Collections;
import java.util.Set;

import org.eclipse.io.prometheusmetrics.internal.OpenHABPrometheusMetricsHandler;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link OpenHABPrometheusMetricsHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Roman Malyugin - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.openhabprometheusmetrics", service = ThingHandlerFactory.class)
public class OpenHABPrometheusMetricsHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_SAMPLE);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_SAMPLE.equals(thingTypeUID)) {
            return new OpenHABPrometheusMetricsHandler(thing);
        }

        return null;
    }
}
