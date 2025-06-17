package com.trimbit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class RandomStringGeneratorImplTest {

  private RandomStringGenerator randomStringGenerator;

  @BeforeEach
  void setUp() {
    randomStringGenerator = new RandomStringGeneratorImpl();
  }

  @Test
  @DisplayName("Should generate random string with expected length")
  void shouldGenerateRandomStringWithExpectedLength() {
    // When
    String result = randomStringGenerator.generateRandomString();

    // Then
    assertEquals(6, result.length(), "Generated string should be 6 characters long");
  }

  @Test
  @DisplayName("Should generate random string with valid characters")
  void shouldGenerateRandomStringWithValidCharacters() {
    // When
    String result = randomStringGenerator.generateRandomString();

    // Then
    assertTrue(
      result.chars().allMatch(c ->
        (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
      ), "Generated string should only contain lowercase letters and numbers"
    );
  }

  @Test
  @DisplayName("Should generate different strings for multiple calls")
  void shouldGenerateDifferentStringsForMultipleCalls() {
    // When
    String result1 = randomStringGenerator.generateRandomString();
    String result2 = randomStringGenerator.generateRandomString();
    String result3 = randomStringGenerator.generateRandomString();

    // Then
    assertNotEquals(result1, result2);
    assertNotEquals(result1, result3);
    assertNotEquals(result2, result3);
  }
}
