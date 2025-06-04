package com.trimbit.server.service;

import javax.enterprise.context.ApplicationScoped;
import java.security.SecureRandom;

@ApplicationScoped
public class RandomStringGeneratorImpl implements RandomStringGenerator {
  private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
  private static final int STRING_LENGTH = 6;
  private final SecureRandom random;

  public RandomStringGeneratorImpl() {
    random = new SecureRandom();
  }

  public String generateRandomString() {
    StringBuilder sb = new StringBuilder(STRING_LENGTH);

    for (int i = 0; i < STRING_LENGTH; i++) {
      int randomIndex = random.nextInt(CHARACTERS.length());
      char randomChar = CHARACTERS.charAt(randomIndex);
      sb.append(randomChar);
    }

    return sb.toString();
  }
}
