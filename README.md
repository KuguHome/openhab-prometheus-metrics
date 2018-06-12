## openHAB bundle for Prometheus metrics

### General objective
Implement a Java/OSGi bundle for Eclipse Smart Home/openHAB, which exposes internal states and data as prometheus metrics. (Probably this won't strictly be a "binding" but rather a "UI" kind of bundle)

### Detailed requirements
* Expose a page at http://localhost:8080/metrics
* Output state of every openHAB thing as seperate metric, using labels (e.g. `openhab_thing_up{thing="zwave:device:15acc881e14:node2"} 1`)

### Additional requirements (later steps)
* Output internal state of Z-wave binding (e.g. send queue) as metrics

### Development environment
* Setting up the openHAB IDE https://www.openhab.org/docs/developer/development/ide.html#prerequisites

### Documentation
* Documentation of prometheus' data format https://prometheus.io/docs/concepts/metric_types/
* Developing Bindings https://www.eclipse.org/smarthome/documentation/development/bindings/how-to.html
* Java library for prometheus https://github.com/prometheus/client_java
* HABmin as a reference how to implement a UI https://github.com/openhab/org.openhab.ui.habmin