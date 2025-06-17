package com.trimbit.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.trimbit.client.config.QueueConfig;
import com.trimbit.model.Stats;
import io.quarkus.runtime.ShutdownEvent;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@ApplicationScoped
public class MessagingServiceImpl implements MessagingService {

  private final Connection connection;
  private final Channel channel;
  private final String sendQueue;
  private final String receiveQueue;
  private final String topicExchange;
  private final Map<String, CompletableFuture<String>> requestToResponse = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Inject
  public MessagingServiceImpl(Connection connection, QueueConfig queueConfig) throws IOException {
    this.connection = connection;
    this.channel = connection.createChannel();
    this.sendQueue = channel.queueDeclare(queueConfig.send(), true, false, false, null).getQueue();
    this.receiveQueue = channel.queueDeclare().getQueue();
    this.topicExchange = queueConfig.topic();
  }

  void onStop(@Observes ShutdownEvent ev) throws IOException, TimeoutException {
    if (this.channel != null) {
      this.channel.close();
    }
    if (this.connection != null) {
      this.connection.close();
    }
  }

  private String publishMessageAndGetResponse(String messageType, String messageToPublish) throws IOException, ExecutionException, InterruptedException, TimeoutException {
    String correlationId = UUID.randomUUID().toString();
    final CompletableFuture<String> response = new CompletableFuture<>();
    this.requestToResponse.put(correlationId, response);
    channel.queueBind(receiveQueue, topicExchange, correlationId);

    var properties = new AMQP.BasicProperties().builder()
      .type(messageType)
      .correlationId(correlationId)
      .build();
    channel.basicPublish("", this.sendQueue, properties, messageToPublish.getBytes(StandardCharsets.UTF_8));

    channel.basicConsume(receiveQueue, true, (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      log.debug("Received message: {}, with routing key {}, correlationId {}", message, delivery.getEnvelope().getRoutingKey(), delivery.getProperties().getCorrelationId());
      this.requestToResponse.get(delivery.getProperties().getCorrelationId()).complete(message);
    }, consumerTag -> {
    });
    String result = response.get(1000, TimeUnit.SECONDS);
    this.requestToResponse.remove(correlationId);
    channel.queueUnbind(receiveQueue, topicExchange, correlationId);

    return result;
  }

  @Override
  public String publishUrl(String user, String longUrl) throws IOException, ExecutionException, InterruptedException, TimeoutException {
    return publishMessageAndGetResponse("publish", String.format("%s|%s", user, longUrl));
  }

  @Override
  public String getUrl(String user, String shortUrl) throws IOException, ExecutionException, InterruptedException, TimeoutException {
    return publishMessageAndGetResponse("get", String.format("%s|%s", user, shortUrl));
  }

  @Override
  public Stats getStats(String user) throws IOException, ExecutionException, InterruptedException, TimeoutException {
    String statsResponse = publishMessageAndGetResponse("stats", user);
    return objectMapper.readValue(statsResponse, Stats.class);
  }
}

