package com.trimbit.client.service;

import com.trimbit.model.Stats;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface MessagingService {
  String publishUrl(String user, String longUrl) throws IOException, ExecutionException, InterruptedException, TimeoutException;

  String getUrl(String user, String shortUrl) throws IOException, ExecutionException, InterruptedException, TimeoutException;

  Stats getStats(String user) throws IOException, ExecutionException, InterruptedException, TimeoutException;
}

