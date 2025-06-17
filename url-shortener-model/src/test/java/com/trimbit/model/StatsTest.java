
package com.trimbit.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

class StatsTest {

  @Test
  void shouldHaveWorkingGettersAndSetters() {
    assertThat(Stats.class, allOf(
      hasValidBeanConstructor(),
      hasValidGettersAndSetters(),
      hasValidBeanHashCode(),
      hasValidBeanEquals(),
      hasValidBeanToString()
    ));
  }
}
