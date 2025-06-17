package com.trimbit.client.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "queue")
public interface QueueConfig {
    String host();
    int port();
    String user();
    String password();
    String vhost();
    String send();
    String topic();
}
