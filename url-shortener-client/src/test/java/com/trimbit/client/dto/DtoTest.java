package com.trimbit.client.dto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;

class DtoTest {

  @ParameterizedTest
  @MethodSource("dtoClasses")
  void testDocumentInput(Class<?> clazz) {
    assertThat(clazz, allOf(
      hasValidBeanConstructor(),
      hasValidGettersAndSetters(),
      hasValidBeanHashCode(),
      hasValidBeanEquals(),
      hasValidBeanToString()));
  }

  static List<Class<?>> dtoClasses() {
    return List.of(
      StatsRequestDto.class,
      StatsResponseDto.class,
      UrlRequestDto.class
    );
  }
}
