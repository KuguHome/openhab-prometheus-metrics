package com.kuguhome.openhab.prometheusmetrics.internal;

import static org.apache.logging.log4j.Level.*;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ops4j.pax.logging.PaxLoggingService;
import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Counter.Child;
import io.prometheus.client.Gauge;

@Component(service = { PaxAppender.class })
public class KuguAppender implements PaxAppender {

    public static class ParseUtil {
        private Pattern nodePattern = Pattern.compile("\\b(\\w*NODE\\w*\\s\\d+)\\b");
        private Pattern queuePattern = Pattern.compile("\\b(\\w*Queue\\slength\\s=\\w*\\s\\d*)\\b");

        private Pattern numberPattern = Pattern.compile("\\b(\\d+)\\b");

        private Pattern nodeAddressInconsistentPattern = Pattern.compile(
                "\\b(\\w*NODE\\s\\d+\\:\\sTransaction\\snot\\scompleted\\:\\snode\\saddress\\sinconsistent\\w*)\\b");

        private Pattern discardedMessagesPattern = Pattern
                .compile("\\b(\\w*Too\\smany\\sretries.\\sDiscarding message\\w*)\\b");

        private Pattern timeoutWhileSendingMessagePattern = Pattern
                .compile("\\b(\\w*NODE\\s\\d+\\:\\sTimeout\\swhile\\ssending\\smessage\\.\\sRequeueing\\w*)\\b");

        private Pattern transactionMismatchPattern = Pattern.compile("\\b(\\w*MISMATCH\\w*)\\b");

        private Pattern globalSendQueuePattern = Pattern.compile(
                "\\b(\\w*Took\\smessage\\sfrom\\squeue\\sfor\\ssending\\.\\sQueue\\slength\\s=\\s\\d+\\w*)\\b");

        private Pattern wakeUpQueuePattern = Pattern
                .compile("\\b(\\w*Is\\sawake\\swith\\s\\d+\\smessages\\sin\\sthe\\swake-up\\squeue\\w*)\\b");

        BiFunction<String, Pattern, String> extract = (s, p) -> {
            Matcher m = p.matcher(s);
            if (m.find()) {
                return m.group(1);
            }
            return "";
        };

        public String getNodeNumber(String logMessage) {
            return extract.apply(extract.apply(logMessage, nodePattern), numberPattern);
        }

        public String getWakeUpQueue(String logMessage) {
            return extract.apply(extract.apply(logMessage, wakeUpQueuePattern), numberPattern);
        }

        public String getNodeAddressInconsistent(String logMessage) {
            return getNodeNumber(extract.apply(logMessage, nodeAddressInconsistentPattern));
        }

        public String getQueueLength(String logMessage) {
            return extract.apply(extract.apply(extract.apply(logMessage, globalSendQueuePattern), queuePattern),
                    numberPattern);
        }

        public boolean isTransactionMismatch(String logMessage) {
            String x = extract.apply(logMessage, transactionMismatchPattern);
            return x.isEmpty() ? false : true;
        }

        public String getTimeoutWhileSending(String logMessage) {
            return getNodeNumber(extract.apply(logMessage, timeoutWhileSendingMessagePattern));
        }

        public String getDiscarded(String logMessage) {
            return getNodeNumber(extract.apply(logMessage, discardedMessagesPattern));
        }
    }

    private static final Counter openhab_zwave_discarded_messages_count = Counter.build()
            .name("openhab_zwave_discarded_messages_count").labelNames("node").register();
    private static final Counter openhab_zwave_transaction_mismatch_count = Counter.build()
            .name("openhab_zwave_transaction_mismatch_count").register();
    private static final Counter openhab_zwave_node_address_inconsistent_counter = Counter.build()
            .name("openhab_zwave_node_address_inconsistent_counter").labelNames("node").register();
    private static final Counter openhab_zwave_timeout_while_sending_message_counter = Counter.build()
            .name("openhab_zwave_timeout_while_sending_message_counter").labelNames("node").register();

    private static final Gauge global_send_queue = Gauge.build("global_send_queue", "global_send_queue")
            .register(CollectorRegistry.defaultRegistry);
    private static final Gauge wake_up_queue = Gauge.build("wake_up_queue", "wake_up_queue").labelNames("node")
            .register(CollectorRegistry.defaultRegistry);

    @Activate
    protected void activate(BundleContext context) {
        final Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(PaxLoggingService.APPENDER_NAME_PROPERTY, "Kugu");
        context.registerService(PaxAppender.class.getName(), this, properties);
    }

    @Override
    public void doAppend(PaxLoggingEvent event) {
        String logMessage = event.getMessage();
        ParseUtil parseUtil = new ParseUtil();

        {
            String node = parseUtil.getDiscarded(logMessage);
            if (!node.isEmpty()) {
                Child child = new Child();
                child.inc();
                openhab_zwave_discarded_messages_count.setChild(child, node);
            }
        }

        {
            if (parseUtil.isTransactionMismatch(logMessage)) {
                Child child = new Child();
                child.inc();
                openhab_zwave_transaction_mismatch_count.setChild(child);
            }
        }

        {
            String length = parseUtil.getQueueLength(logMessage);
            if (!length.isEmpty()) {
                Gauge.Child child = new Gauge.Child();
                child.set(Double.parseDouble(length));
                global_send_queue.setChild(child);
            }
        }
        {
            String node = parseUtil.getNodeNumber(logMessage);
            if (!node.isEmpty()) {
                Child child = new Child();
                child.inc();
                openhab_zwave_node_address_inconsistent_counter.setChild(child, node);
            }
        }
        {
            String parsed = parseUtil.getWakeUpQueue(logMessage);
            String node = parseUtil.getNodeNumber(logMessage);
            if (!parsed.isEmpty() && !node.isEmpty()) {
                Gauge.Child child = new Gauge.Child();
                child.set(Double.parseDouble(parsed));
                wake_up_queue.setChild(child, node);
            }
        }
        {
            String parsed = parseUtil.getTimeoutWhileSending(logMessage);
            String node = parseUtil.getNodeNumber(logMessage);
            if (!parsed.isEmpty() && !node.isEmpty()) {
                Child child = new Child();
                child.inc();
                openhab_zwave_timeout_while_sending_message_counter.setChild(child, node);
            }
        }

        if (TRACE.name().equals(event.getLevel().toString())) {
            TRACE_LABEL.inc();
        } else if (DEBUG.name().equals(event.getLevel().toString())) {
            DEBUG_LABEL.inc();
        } else if (INFO.name().equals(event.getLevel().toString())) {
            INFO_LABEL.inc();
        } else if (WARN.name().equals(event.getLevel().toString())) {
            WARN_LABEL.inc();
        } else if (ERROR.name().equals(event.getLevel().toString())) {
            ERROR_LABEL.inc();
        } else if (FATAL.name().equals(event.getLevel().toString())) {
            FATAL_LABEL.inc();
        } else {
            System.out.println("Level: " + event.getLevel().toString());
        }

    }

    public static final String COUNTER_NAME = "openhab_logmessage_count";

    private static final Counter COUNTER;
    private static final Counter.Child TRACE_LABEL;
    private static final Counter.Child DEBUG_LABEL;
    private static final Counter.Child INFO_LABEL;
    private static final Counter.Child WARN_LABEL;
    private static final Counter.Child ERROR_LABEL;
    private static final Counter.Child FATAL_LABEL;

    static {
        COUNTER = Counter.build().name(COUNTER_NAME).help("logmessage count at various log levels").labelNames("level")
                .register();

        TRACE_LABEL = COUNTER.labels("trace");
        DEBUG_LABEL = COUNTER.labels("debug");
        INFO_LABEL = COUNTER.labels("info");
        WARN_LABEL = COUNTER.labels("warn");
        ERROR_LABEL = COUNTER.labels("error");
        FATAL_LABEL = COUNTER.labels("fatal");
    }

}
