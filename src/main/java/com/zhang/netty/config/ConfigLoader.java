package com.zhang.netty.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class ConfigLoader {
    private static ConfigLoader instance = new ConfigLoader();

    public static ConfigLoader getInstance() {
        return instance;
    }

    private ConfigLoader() {
    }

    private String server;
    private int port;
    private String threadName;
    private int coreThreadNum;
    private int maxThreadNum;
    private int queueNum;

    private int writerIdleTime;
    private int readerIdleTime;
    private int allIdleTime;

    public boolean load() {
        try {
            Properties prop = new Properties();
            prop.load(prop.getClass().getResourceAsStream("/client.properties"));
            server = prop.getProperty("sever", "127.0.0.1");
            port = Integer.parseInt(prop.getProperty("port", "8000"));
            threadName = prop.getProperty("thread.name", "zhang");
            maxThreadNum = Integer.parseInt(prop.getProperty("max.thread.num", "64"));
            queueNum = Integer.parseInt(prop.getProperty("queue.num", "64"));
            coreThreadNum = Integer.parseInt(prop.getProperty("core.thread.num", "64"));
            writerIdleTime = Integer.parseInt(prop.getProperty("writer.idle.time", "0"));
            readerIdleTime = Integer.parseInt(prop.getProperty("reader.idle.time", "0"));
            allIdleTime = Integer.parseInt(prop.getProperty("all.idle.time", "60"));
            return true;
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getThreadName() {
        return threadName;
    }

    public int getMaxThreadNum() {
        return maxThreadNum;
    }

    public int getQueueNum() {
        return queueNum;
    }

    public int getCoreThreadNum() {
        return coreThreadNum;
    }

    public int getWriterIdleTime() {
        return writerIdleTime;
    }

    public int getReaderIdleTime() {
        return readerIdleTime;
    }

    public int getAllIdleTime() {
        return allIdleTime;
    }
}
