package com.zhang.netty;

import com.zhang.netty.config.ConfigLoader;
import com.zhang.netty.server.NettyServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Starter {

    public static void main(String[] args) {
        ConfigLoader loader = ConfigLoader.getInstance();
        if (!loader.load()) {
            log.error("Can't find config file");
        }
        NettyServer ns = new NettyServer();
        try {
            ns.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
