package org.openhab.binding.openhabprometheusmetrics.prometheus;

import io.prometheus.client.Counter;

public class Client {

    static final Counter requests = Counter.build().name("requests_total").help("Total requests.").register();

    void processRequest() {
        requests.inc();
        // Your code here.
    }

    public double getTotal() {
        return requests.get();
    }

}
