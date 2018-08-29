package com.kuguhome.openhab.prometheusmetrics;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.kuguhome.openhab.prometheusmetrics.internal.KuguAppender;
import com.kuguhome.openhab.prometheusmetrics.internal.KuguAppender.ParseUtil;

public class ZwavePatternTest {

    private String testDiscardMessage = "./openhab.log.6:2018-04-03 08:54:26.144 [DEBUG] [ocol.ZWaveController$ZWaveSendThread] - NODE 7: Too many retries. Discarding message: Message: class=SendData[0x13], type=Request[0x00], priority=Get, dest=7, callback=56, payload=07 07 60 0D 01 03 32 01 20";
    private String testMismatch = "./openhab.log.5:2018-04-03 15:32:42.068 [DEBUG] [.serialmessage.ZWaveCommandProcessor] - Checking transaction complete: class=SendData, callback id=147, expected=ApplicationCommandHandler, cancelled=false      MISMATCH";
    private String testInconsistent = "./openhab.log.6:2018-04-03 07:56:42.873 [DEBUG] [ssage.ApplicationCommandMessageClass] - NODE 13: Transaction not completed: node address inconsistent.  lastSent=13, incoming=255";
    private String testQueueLength = "openhab.log.7:2018-07-04 05:40:22.237 [DEBUG] [ocol.ZWaveController$ZWaveSendThread] - Took message from queue for sending. Queue length = 45";

    private String testWakeUpQueue = "./openhab.log.6:2018-04-03 10:22:46.462 [DEBUG] [commandclass.ZWaveWakeUpCommandClass] - NODE 48: Message already on the wake-up queue. Removing original.\n"
            + "./openhab.log.6:2018-04-03 10:22:37.322 [DEBUG] [commandclass.ZWaveWakeUpCommandClass] - NODE 56: Is awake with 7 messages in the wake-up queue.";

    private String testTimeout = "./openhab.log.5:2018-04-03 16:53:52.070 [DEBUG] [ocol.ZWaveController$ZWaveSendThread] - NODE 7: Timeout while sending message. Requeueing - 2 attempts left!";

    private ParseUtil parseUtil;

    @Before
    public void init() {
        parseUtil = new KuguAppender.ParseUtil();
    }

    @Test
    public void testDiscardMessage() {
        assertEquals("7", parseUtil.getNodeNumber(testDiscardMessage));
    }

    @Test
    public void testMismatch() {
        assertTrue(parseUtil.isTransactionMismatch(testMismatch));
    }

    @Test
    public void testWakeUpQueue() {
        assertEquals("48", parseUtil.getNodeNumber(testWakeUpQueue));
        assertEquals("7", parseUtil.getWakeUpQueue(testWakeUpQueue));
    }

    @Test
    public void testInconsistent() {
        assertEquals("13", parseUtil.getNodeAddressInconsistent(testInconsistent));
    }

    @Test
    public void testQueueLength() {
        assertEquals("45", parseUtil.getQueueLength(testQueueLength));
    }

    @Test
    public void testTimeout() {
        assertEquals("7", parseUtil.getTimeoutWhileSending(testTimeout));
    }
}
