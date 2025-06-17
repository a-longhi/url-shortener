package com.trimbit.client.resource;

import com.trimbit.client.dto.StatsRequestDto;
import com.trimbit.client.dto.StatsResponseDto;
import com.trimbit.client.dto.UrlRequestDto;
import com.trimbit.client.mapper.StatsMapper;
import com.trimbit.client.service.MessagingService;
import com.trimbit.client.service.MessagingServiceImpl;
import com.trimbit.model.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class UrlResourceTest {

  private MessagingService messagingService;
  private StatsMapper statsMapper;
  private UrlResource urlResource;

  @BeforeEach
  void setup(){
    this.messagingService = mock(MessagingServiceImpl.class);
    this.statsMapper = mock(StatsMapper.class);
    this.urlResource = new UrlResource(messagingService, statsMapper);
  }

  @Test
  void shouldCreateUrlWhenValidRequest() throws IOException, ExecutionException, InterruptedException, TimeoutException {
    // GIVEN
    String user = "fancy@email.com";
    String longUrl = "longUrl";
    String expectedUrl = "shortUrl";

    var urlRequestDto = new UrlRequestDto();
    urlRequestDto.setUrl(longUrl);
    urlRequestDto.setUserEmail(user);
    when(messagingService.publishUrl(user, longUrl)).thenReturn(expectedUrl);

    // WHEN
    String actualUrl = urlResource.createUrl(urlRequestDto);

    // THEN
    verify(messagingService).publishUrl(user, longUrl);
    verifyNoMoreInteractions(messagingService);
    assertEquals(expectedUrl, actualUrl);
  }

  @Test
  void retrieveUrlWhenValidRequest() throws IOException, ExecutionException, InterruptedException, TimeoutException {
    // GIVEN
    String user = "fancy@email.com";
    String shortUrl = "shortUrl";
    String expectedUrl = "longUrl";

    var urlRequestDto = new UrlRequestDto();
    urlRequestDto.setUrl(shortUrl);
    urlRequestDto.setUserEmail(user);
    when(messagingService.getUrl(user, shortUrl)).thenReturn(expectedUrl);

    // WHEN
    String actualUrl = urlResource.retrieveUrl(urlRequestDto);

    // THEN
    verify(messagingService).getUrl(user, shortUrl);
    verifyNoMoreInteractions(messagingService);
    assertEquals(expectedUrl, actualUrl);
  }

  @Test
  void retrieveStatsWhenValidRequest() throws IOException, ExecutionException, InterruptedException, TimeoutException {
    // GIVEN
    String user = "fancy@email.com";
    Stats expectedStats = new Stats();
    expectedStats.setInsertions(5);
    expectedStats.setShortUrlCount(Map.of("short1", 1, "short2", 2));

    var statsRequestDto = new StatsRequestDto();
    statsRequestDto.setUserEmail(user);
    when(messagingService.getStats(user)).thenReturn(expectedStats);
    StatsResponseDto expectedDto = new StatsResponseDto();
    expectedDto.setInsertions(5);
    expectedDto.setShortUrlCount(Map.of("short1", 1, "short2", 2));
    when(statsMapper.statsToStatsResponseDto(expectedStats)).thenReturn(expectedDto);

    // WHEN
    StatsResponseDto actualStats = urlResource.retrieveStats(statsRequestDto);

    // THEN
    verify(messagingService).getStats(user);
    verify(statsMapper).statsToStatsResponseDto(expectedStats);
    verifyNoMoreInteractions(messagingService, statsMapper);
    assertEquals(expectedDto.getInsertions(), actualStats.getInsertions());
    assertEquals(expectedDto.getShortUrlCount(), actualStats.getShortUrlCount());
  }
}

