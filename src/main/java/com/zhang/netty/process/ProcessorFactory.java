package com.zhang.netty.process;

import com.zhang.netty.process.model.EventContext;
import com.zhang.netty.process.strategy.EventProcessorV1;
import com.zhang.netty.protocol.NettyProtocol;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

public class ProcessorFactory {
    private static final Map<Byte, EventProcessor> processorMap = new HashMap<>();

    @PostConstruct
    private void init() {
        processorMap.put(EventProcessorV1.VERSION, new EventProcessorV1());
    }

    public static void processEvent(NettyProtocol protocol, EventContext eventContext) {
        processorMap.get(protocol.getVersion()).processEvent(protocol, eventContext);
    }
}
