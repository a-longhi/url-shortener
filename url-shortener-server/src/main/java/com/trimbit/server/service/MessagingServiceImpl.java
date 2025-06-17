package com.trimbit.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.trimbit.model.Stats;
import io.quarkus.runtime.ShutdownEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
@Slf4j
public class MessagingServiceImpl implements MessagingService {
  private final Connection connection;
  private final Channel channel;
  private final String receiveQueue;
  private final String topicExchange;
  private final UrlService urlService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public MessagingServiceImpl(@ConfigProperty(name = "queue.host") String host,
                              @ConfigProperty(name = "queue.port") int port,
                              @ConfigProperty(name = "queue.user") String user,
                              @ConfigProperty(name = "queue.password") String password,
                              @ConfigProperty(name = "queue.vhost") String vhost,
                              @ConfigProperty(name = "queue.receive") String receive,
                              @ConfigProperty(name = "queue.topic") String topicExchange,
                              UrlService urlService) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    factory.setPort(port);
    factory.setUsername(user);
    factory.setPassword(password);
    factory.setVirtualHost(vhost);
    this.connection = factory.newConnection();
    this.channel = connection.createChannel();
    this.channel.basicQos(1);
    this.receiveQueue = channel.queueDeclare(receive, true, false, false, null).getQueue();
    this.topicExchange = topicExchange;
    this.urlService = urlService;
  }
  void onStop(@Observes ShutdownEvent ev) throws IOException, TimeoutException {
    if (this.channel != null) {
      this.channel.close();
    }
    if (this.connection != null) {
      this.connection.close();
    }
  }

  public void startProcessingMessages() throws IOException {
    log.debug("Awaiting requests");

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      AMQP.BasicProperties replyProps = new AMQP.BasicProperties
        .Builder()
        .correlationId(delivery.getProperties().getCorrelationId())
        .build();

      try {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        String[] messageParts = message.split("\\|");
        String topic = delivery.getProperties().getCorrelationId();

        log.debug("Received message: {} with correlationId {}", message, delivery.getProperties().getCorrelationId());
        String type = delivery.getProperties().getType();
        switch (type) {
          case "publish" -> processPublish(messageParts, topic, replyProps);
          case "get" -> processGet(messageParts, topic, replyProps);
          case "stats" -> processStats(message, topic, replyProps);
          default -> throw new IllegalStateException(String.format("%s message not recognized", type));
        }
      } catch (RuntimeException e) {
        log.error(e.toString());
        throw e;
      }
    };

    channel.basicConsume(this.receiveQueue, true, deliverCallback, (consumerTag -> {}));
  }

  private void processPublish(String[] messageParts, String topic, AMQP.BasicProperties replyProps) throws IOException {
    Optional<String> shortUrl = urlService.createUrl(messageParts[0], messageParts[1]);
    channel.basicPublish(topicExchange, topic, replyProps, shortUrl.orElse("ERROR").getBytes(StandardCharsets.UTF_8));
  }

  private void processGet(String[] messageParts, String topic, AMQP.BasicProperties replyProps) throws IOException {
    Optional<String> longUrl = urlService.getUrl(messageParts[0], messageParts[1]);
    channel.basicPublish(topicExchange, topic, replyProps, longUrl.orElse("ERROR").getBytes(StandardCharsets.UTF_8));
  }

  private void processStats(String user, String topic, AMQP.BasicProperties replyProps) {
    Stats stats = urlService.getStats(user);
    try {
      String jsonMessage = objectMapper.writeValueAsString(stats);
      channel.basicPublish(topicExchange, topic, replyProps, jsonMessage.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      try {
        channel.basicPublish(topicExchange, topic, replyProps, "ERROR".getBytes(StandardCharsets.UTF_8));
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      throw new RuntimeException(e);
      }
  }
}

