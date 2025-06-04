package com.trimbit.server.service;

import com.trimbit.server.model.Stats;
import io.quarkus.runtime.ShutdownEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class UrlServiceImpl implements UrlService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UrlServiceImpl.class);
  public static final String USER_KEY = "user";
  public static final String SHORT_URL_KEY = "shortUrl";
  public static final String LONG_URL_KEY = "longUrl";
  public static final String INSERTIONS_KEY = "insertions";
  private final RandomStringGenerator randomStringGenerator;
  private final JedisPooled jedis;

  public UrlServiceImpl(@ConfigProperty(name = "redis.host") String redisHost,
                        @ConfigProperty(name = "redis.port") int redisPort,
                        RandomStringGenerator randomStringGenerator) {
    this.randomStringGenerator = randomStringGenerator;
    this.jedis = new JedisPooled(redisHost, redisPort);
  }

  void onStop(@Observes ShutdownEvent ev) {
    this.jedis.close();
  }

  @Override
  public Optional<String> createUrl(String user, String longUrl) {
    var association = jedis.hgetAll(longUrl);
    if (association.size() == 0) {
      String shortUrl = randomStringGenerator.generateRandomString();
      jedis.hset(longUrl, Map.of(USER_KEY, user, SHORT_URL_KEY, shortUrl));
      jedis.hset(shortUrl, Map.of(USER_KEY, user, LONG_URL_KEY, longUrl));

      // number of insertions per user
      var stats = jedis.hgetAll(user);
      if (stats.size() == 0){
        jedis.hset(user, Map.of(INSERTIONS_KEY, "1", shortUrl, "0"));
      }
      else {
        String newInsertions = String.valueOf(Integer.parseInt(stats.get(INSERTIONS_KEY)) + 1);
        stats.put(INSERTIONS_KEY, newInsertions);
        stats.put(shortUrl, "0");
        jedis.hset(user, stats);
      }

      LOGGER.debug("Creating new association with user: {}, longUrl: {}, shortUrl: {}", user, longUrl, shortUrl);
      return Optional.of(shortUrl);
    } else {
      LOGGER.debug("Association for longUrl: {} present", longUrl);
      String expectedUser = association.get(USER_KEY);
      if (expectedUser.equals(user)) {
        var shortUrl = association.get(SHORT_URL_KEY);
        LOGGER.debug("Returning existing association with user: {}, longUrl: {}, shortUrl: {}", user, longUrl, shortUrl);
        return Optional.of(shortUrl);
      } else {
        LOGGER.debug("User didn't match. expectedUser: {}, actualUser: {}", expectedUser, user);
        return Optional.empty();
      }
    }
  }

  @Override
  public Optional<String> getUrl(String user, String shortUrl) {
    var association = jedis.hgetAll(shortUrl);
    if (association.size() == 0) {
      LOGGER.debug("Association for shortUrl: {} not present", shortUrl);
      return Optional.empty();
    }
    else {
      LOGGER.debug("Association for shortUrl: {} present", shortUrl);
      String expectedUser = association.get(USER_KEY);
      if (expectedUser.equals(user)) {

        var stats = jedis.hgetAll(user);
        if (stats.size() == 0){
          jedis.hset(user, Map.of(INSERTIONS_KEY, "0", shortUrl, "1"));
        }
        else {
          String newTimeAsked = String.valueOf(Integer.parseInt(stats.get(shortUrl)) + 1);
          stats.put(shortUrl, newTimeAsked);
          jedis.hset(user, stats);
        }

        var longUrl = association.get(LONG_URL_KEY);
        LOGGER.debug("Returning existing association with user: {}, longUrl: {}, shortUrl: {}", user, longUrl, shortUrl);
        return Optional.of(longUrl);
      } else {
        LOGGER.debug("User didn't match. expectedUser: {}, actualUser: {}", expectedUser, user);
        return Optional.empty();
      }
    }
  }

  @Override
  public Stats getStats(String user) {
    var statMap = jedis.hgetAll(user);
    if (statMap.size() == 0) {
      LOGGER.debug("Stats for user: {} not present. Creating blank", user);
      statMap = Map.of(INSERTIONS_KEY, "0");
      jedis.hset(user, statMap);
    }
    Stats stats = new Stats();
    stats.setInsertions(Integer.parseInt(statMap.get(INSERTIONS_KEY)));
    Map<String, Integer> timeAskedPerShortUrl = statMap.entrySet()
      .stream()
      .filter(entry -> !entry.getKey().equals(INSERTIONS_KEY))
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        entry -> Integer.parseInt(entry.getValue())
      ));
    stats.setShortUrlCount(timeAskedPerShortUrl);
    LOGGER.debug("Returning stats for user: {}", user);
    return stats;
  }
}
