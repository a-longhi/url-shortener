package com.trimbit.client.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@ApplicationScoped
public class RabbitMQConnectionProducer {

    @Produces
    @ApplicationScoped
    public Connection rabbitMQConnection(QueueConfig queueConfig) throws IOException, TimeoutException {
        log.info("Creating RabbitMQ connection to {}:{}", queueConfig.host(), queueConfig.port());
        ConnectionFactory factory = createConnectionFactory(queueConfig);
        return factory.newConnection();
    }

    private ConnectionFactory createConnectionFactory(QueueConfig queueConfig) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(queueConfig.host());
        factory.setPort(queueConfig.port());
        factory.setUsername(queueConfig.user());
        factory.setPassword(queueConfig.password());
        factory.setVirtualHost(queueConfig.vhost());
        return factory;
    }
}
