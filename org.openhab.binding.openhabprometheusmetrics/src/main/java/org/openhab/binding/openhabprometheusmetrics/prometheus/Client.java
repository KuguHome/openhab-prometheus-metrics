package org.openhab.binding.openhabprometheusmetrics.prometheus;

import io.prometheus.client.Counter;

public class Client {

    static final Counter openhabBundleState = Counter.build().name("openhab_bundle_state").help("OSGi bundles state")
            .register();

    /*
     * void processRequest() {
     * requests.inc();
     * // Your code here.
     * }
     * 
     * public double getTotal() {
     * return requests.get();
     * }
     */

}
