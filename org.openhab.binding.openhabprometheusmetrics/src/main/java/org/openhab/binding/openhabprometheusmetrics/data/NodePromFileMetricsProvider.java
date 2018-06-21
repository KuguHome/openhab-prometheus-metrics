package org.openhab.binding.openhabprometheusmetrics.data;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class NodePromFileMetricsProvider implements MetricsProvider<MetricItem> {

    private static final String PROPERTIES_DELIMETER = " ";
    private static final String COMMENTED_PREFIX = "#";
    private static final String FILE_NAME = "node.prom.txt";

    @Override
    public Stream<MetricItem> getMetrics() throws Exception {

        List<MetricItem> list = new LinkedList<MetricItem>();

        try (Stream<String> stream = Files.lines(Paths.get(FILE_NAME))) {
            stream.forEach(line -> {
                if (!line.startsWith(COMMENTED_PREFIX)) {
                    String[] splitted = line.split(PROPERTIES_DELIMETER);
                    MetricItem metricItem = new MetricItem(splitted[0], splitted[1]);
                    list.add(metricItem);
                }
            });
        }
        return list.stream();
    }

}
