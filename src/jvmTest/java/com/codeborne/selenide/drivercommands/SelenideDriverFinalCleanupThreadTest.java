package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Config;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

final class SelenideDriverFinalCleanupThreadTest {
  @Test
  void closesDriverAndProxy() {
    Config config = mock(Config.class);
    WebDriver driver = mock(WebDriver.class);
    CloseDriverCommand closeDriverCommand = mock(CloseDriverCommand.class);

    new SelenideDriverFinalCleanupThread(config, driver, null, closeDriverCommand).invoke();

    verify(closeDriverCommand).close(config, driver, null);
    verifyNoMoreInteractions(closeDriverCommand);
    verifyNoInteractions(config);
    verifyNoInteractions(driver);
  }
}
