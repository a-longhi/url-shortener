package com.trimbit.model;

import lombok.Data;
import java.util.Map;

@Data
public class Stats {
  private Integer insertions;
  private Map<String, Integer> shortUrlCount;
}
