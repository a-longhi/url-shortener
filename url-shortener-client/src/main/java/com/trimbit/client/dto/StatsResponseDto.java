package com.trimbit.client.dto;

import java.util.Map;
import java.util.Objects;

public class StatsResponseDto {

  private Integer insertions;

  private Map<String, Integer> shortUrlCount;

  public Integer getInsertions() {
    return insertions;
  }

  public void setInsertions(Integer insertions) {
    this.insertions = insertions;
  }

  public Map<String, Integer> getShortUrlCount() {
    return shortUrlCount;
  }

  public void setShortUrlCount(Map<String, Integer> shortUrlCount) {
    this.shortUrlCount = shortUrlCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StatsResponseDto that = (StatsResponseDto) o;
    return Objects.equals(insertions, that.insertions) && Objects.equals(shortUrlCount, that.shortUrlCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(insertions, shortUrlCount);
  }

  @Override
  public String toString() {
    return "StatsResponseDto{" +
      "insertions=" + insertions +
      ", shortUrlCount=" + shortUrlCount +
      '}';
  }
}
