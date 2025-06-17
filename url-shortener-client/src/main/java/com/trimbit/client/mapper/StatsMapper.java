package com.trimbit.client.mapper;

import com.trimbit.client.dto.StatsResponseDto;
import com.trimbit.model.Stats;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface StatsMapper {

    StatsResponseDto statsToStatsResponseDto(Stats stats);
}


