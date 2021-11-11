package com.zhang.netty.process.strategy;

import com.zhang.netty.process.EventProcessor;
import com.zhang.netty.process.model.EventContext;
import com.zhang.netty.protocol.NettyProtocol;

public class EventProcessorV1 implements EventProcessor {

    @Override
    public void processEvent(NettyProtocol protocol, EventContext eventContext) {
        switch(protocol.getApiKey()) {

        }
    }

}
