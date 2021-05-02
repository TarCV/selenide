package com.codeborne.selenide.impl;

import java.util.List;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.DriverStub;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class FilteringCollectionTest implements WithAssertions {
  @Test
  void getActualElement() {
    WebElement mockedWebElement1 = mock(WebElement.class);
    WebElement mockedWebElement2 = mock(WebElement.class);
    final DriverStub driver = new DriverStub();

    when(mockedWebElement1.isDisplayed()).thenReturn(false);
    when(mockedWebElement2.isDisplayed()).thenReturn(true);

    CollectionSource mockedCollection = mock(CollectionSource.class);
    when(mockedCollection.getElements()).thenReturn(asList(mockedWebElement1, mockedWebElement2));
    when(mockedCollection.driver()).thenReturn(driver);
    FilteringCollection filteringCollection = new FilteringCollection(mockedCollection, Condition.visible);

    List<WebElement> actualElements = filteringCollection.getElements();
    assertThat(actualElements)
      .hasSize(1);
    assertThat(actualElements.get(0))
      .isEqualTo(mockedWebElement2);
  }

  @Test
  void description() {
    CollectionSource mockedCollection = mock(CollectionSource.class);
    when(mockedCollection.description()).thenReturn("Collection description");
    FilteringCollection filteringCollection = new FilteringCollection(mockedCollection, Condition.visible);
    assertThat(filteringCollection.description())
      .isEqualTo("Collection description.filter(visible)");
  }
}
