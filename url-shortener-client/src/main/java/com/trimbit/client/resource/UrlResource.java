package com.trimbit.client.resource;

import com.trimbit.client.dto.StatsResponseDto;
import com.trimbit.client.dto.UrlRequestDto;
import com.trimbit.client.dto.StatsRequestDto;
import com.trimbit.client.model.Stats;
import com.trimbit.client.service.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Path("/v1/urls")
public class UrlResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(UrlResource.class);

  private final MessagingService messagingService;

  public UrlResource(MessagingService messagingService) {
    this.messagingService = messagingService;
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String createUrl(UrlRequestDto urlRequestDto) {
    LOGGER.debug("Create url: {}", urlRequestDto);
    String shortUrl;
    try {
      shortUrl = messagingService.publishUrl(urlRequestDto.getUserEmail(), urlRequestDto.getUrl());
    } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
    return shortUrl;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/query")
  public String retrieveUrl(UrlRequestDto urlRequestDto){
    LOGGER.debug("Retrieve url: {}", urlRequestDto);
    String longUrl;
    try {
      longUrl = messagingService.getUrl(urlRequestDto.getUserEmail(), urlRequestDto.getUrl());
    } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
    return longUrl;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/stats")
  public StatsResponseDto retrieveStats(StatsRequestDto statsRequestDto){
    LOGGER.debug("Retrieve stats: {}", statsRequestDto);
    Stats stats;
    try {
      stats = messagingService.getStats(statsRequestDto.getUserEmail());
    } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
    StatsResponseDto statsResponse = new StatsResponseDto();
    statsResponse.setInsertions(stats.getInsertions());
    statsResponse.setShortUrlCount(stats.getShortUrlCount());
    return statsResponse;
  }
}

// on startup: adds his receive queue
// on request: generates UUID - binds the topic exchange to the UUID - receives the messages - unbinds UUID
// on kill: deletes the receive queue
