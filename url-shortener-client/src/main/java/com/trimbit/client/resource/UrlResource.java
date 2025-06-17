package com.trimbit.client.resource;

import com.trimbit.client.dto.StatsResponseDto;
import com.trimbit.client.dto.UrlRequestDto;
import com.trimbit.client.dto.StatsRequestDto;
import com.trimbit.client.service.MessagingService;
import com.trimbit.client.mapper.StatsMapper;
import com.trimbit.model.Stats;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Path("/v1/urls")
public class UrlResource {

  private final StatsMapper statsMapper;
  private final MessagingService messagingService;

  public UrlResource(MessagingService messagingService, StatsMapper statsMapper) {
    this.messagingService = messagingService;
    this.statsMapper = statsMapper;
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String createUrl(UrlRequestDto urlRequestDto) {
    log.debug("Create url: {}", urlRequestDto);
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
    log.debug("Retrieve url: {}", urlRequestDto);
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
    log.debug("Retrieve stats: {}", statsRequestDto);
    Stats stats;
    try {
      stats = messagingService.getStats(statsRequestDto.getUserEmail());
    } catch (IOException | ExecutionException | InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
    return statsMapper.statsToStatsResponseDto(stats);
  }
}

// on startup: adds his receive queue
// on request: generates UUID - binds the topic exchange to the UUID - receives the messages - unbinds UUID
// on kill: deletes the receive queue

