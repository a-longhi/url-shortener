package com.trimbit.client.dto;

import java.util.Objects;

public class UrlRequestDto {

    private String url;
    private String userEmail;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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
        UrlRequestDto that = (UrlRequestDto) o;
        return Objects.equals(url, that.url) && Objects.equals(userEmail, that.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, userEmail);
    }

    @Override
    public String toString() {
        return "CreateUrlRequestDto{" +
                "url='" + url + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
