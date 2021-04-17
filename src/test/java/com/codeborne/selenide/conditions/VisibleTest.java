package com.codeborne.selenide.conditions;

import com.codeborne.selenide.DriverStub;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class VisibleTest {
  private final Visible condition = new Visible();
  private final WebElement element = mock(WebElement.class);

  @Test
  void negate() {
    assertThat(condition.missingElementSatisfiesCondition()).isFalse();
    assertThat(condition.negate().missingElementSatisfiesCondition()).isTrue();
  }

  @Test
  void name() {
    assertThat(condition.getName()).isEqualTo("visible");
    assertThat(condition.negate().getName()).isEqualTo("not visible");
  }

  @Test
  void satisfied_if_element_is_visible() {
    when(element.isDisplayed()).thenReturn(true);
    assertThat(condition.apply(new DriverStub(), element)).isTrue();
  }

  @Test
  void not_satisfied_if_element_is_invisible() {
    when(element.isDisplayed()).thenReturn(false);
    assertThat(condition.apply(new DriverStub(), element)).isFalse();
  }

  @Test
  void actualValue_invisible() {
    assertThat(condition.actualValue(new DriverStub(), element)).isEqualTo("visible:false");
  }

  @Test
  void actualValue_visible() {
    when(element.isDisplayed()).thenReturn(true);
    assertThat(condition.actualValue(new DriverStub(), element)).isEqualTo("visible:true");
  }
}
