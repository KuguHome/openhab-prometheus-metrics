package org.openhab.binding.openhabprometheusmetrics.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.internal.ThingRegistryImpl;
import org.eclipse.smarthome.core.thing.internal.ThingTracker;
import org.osgi.service.component.ComponentContext;

//@Component(immediate = true)
public class OpenHABPrometheusMetricsThingManager implements ThingTracker {

    // private ThingManager thingManager;

    private Map<ThingUID, Thing> thingsPool = new ConcurrentHashMap<>();

    private ThingRegistryImpl thingRegistry;

    @Override
    public void thingAdded(Thing thing, ThingTrackerEvent thingTrackerEvent) {
        // super.thingAdded(thing, thingTrackerEvent);
        thingsPool.put(thing.getUID(), thing);
    }

    @Override
    public void thingRemoving(Thing thing, ThingTrackerEvent thingTrackerEvent) {
        // super.thingRemoving(thing, thingTrackerEvent);
        thingsPool.put(thing.getUID(), thing);
    }

    @Override
    public void thingRemoved(Thing thing, ThingTrackerEvent thingTrackerEvent) {
        thingsPool.remove(thing.getUID());

    }

    @Override
    public void thingUpdated(Thing thing, ThingTrackerEvent thingTrackerEvent) {
        thingsPool.put(thing.getUID(), thing);
    }

    // @Activate
    protected synchronized void activate(ComponentContext componentContext) {
        // super.activate(componentContext);
        thingRegistry.addThingTracker(this);
    }

    // @Reference
    protected void setThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = (ThingRegistryImpl) thingRegistry;
    }

    protected void unsetThingRegistry(ThingRegistry thingRegistry) {
        this.thingRegistry = null;
    }

    public Set<Thing> getThingSet() {
        return new HashSet<>(thingsPool.values());
    }

}
