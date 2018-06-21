package org.openhab.binding.openhabprometheusmetrics.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class NodeFileReader {

    public Stream<String> read(String fileName) throws IOException {

        return Files.lines(Paths.get(fileName));

    }

}
