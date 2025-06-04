package com.trimbit.client.dto;

import java.util.Objects;

public class StatsRequestDto {

  private String userEmail;

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StatsRequestDto that = (StatsRequestDto) o;
    return Objects.equals(userEmail, that.userEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userEmail);
  }

  @Override
  public String toString() {
    return "StatsRequestDto{" +
      "userEmail='" + userEmail + '\'' +
      '}';
  }
}
