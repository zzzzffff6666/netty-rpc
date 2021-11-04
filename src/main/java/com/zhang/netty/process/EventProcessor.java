package com.zhang.netty.process;

import com.zhang.netty.process.model.EventContext;
import com.zhang.netty.protocol.NettyProtocol;

public interface EventProcessor {

    void processEvent(NettyProtocol protocol, EventContext eventContext);

}
