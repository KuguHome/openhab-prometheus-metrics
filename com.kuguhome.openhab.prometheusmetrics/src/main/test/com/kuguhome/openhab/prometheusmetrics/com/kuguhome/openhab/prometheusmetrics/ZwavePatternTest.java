package com.kuguhome.openhab.prometheusmetrics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class ZwavePatternTest {

    private String openhab_zwave_discarded_messages_count = "./openhab.log.6:2018-04-03 08:54:26.144 [DEBUG] [ocol.ZWaveController$ZWaveSendThread] - NODE 7: Too many retries. Discarding message: Message: class=SendData[0x13], type=Request[0x00], priority=Get, dest=7, callback=56, payload=07 07 60 0D 01 03 32 01 20";

    private Pattern discardedMessagesPattern = Pattern
            .compile("\\b(\\w*Too\\smany\\sretries.\\sDiscarding message\\w*)\\b");

    private Pattern nodePattern = Pattern.compile("\\b(\\w*NODE\\w*\\s\\d)\\b");
    Matcher matcher = pattern.matcher(openhab_zwave_discarded_messages_count);

    @Test
    public void patternTest() {

        if (openhab_zwave_discarded_messages_count.contains(openhab_zwave_discarded_messages_count_pattern_match)) {
            if (matcher.find()) {
                System.out.println(matcher.group(1));
            }
        }
    }

}
