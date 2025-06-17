package com.trimbit.server.service;


import com.trimbit.model.Stats;

import java.util.Optional;

public interface UrlService {
  Optional<String> createUrl(String messagePart, String messagePart1);
  Optional<String> getUrl(String messagePart, String messagePart1);
  Stats getStats(String user);
}

