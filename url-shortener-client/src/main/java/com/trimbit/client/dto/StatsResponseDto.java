package com.trimbit.client.dto;

import lombok.Data;
import java.util.Map;

@Data
public class StatsResponseDto {
    private Integer insertions;
    private Map<String, Integer> shortUrlCount;
}
