package com.zhang.netty;

import com.zhang.netty.client.NettyClient;
import com.zhang.netty.config.ConfigLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Starter {

    public static void main(String[] args) {
        ConfigLoader loader = ConfigLoader.getInstance();
        if (!loader.load()) {
            log.error("Can't find config file");
        }
        NettyClient nettyClient = new NettyClient();
        try {
            nettyClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
