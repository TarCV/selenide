package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

import static org.mockito.Mockito.mock;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS;
import static org.openqa.selenium.remote.CapabilityType.ACCEPT_SSL_CERTS;
import static org.openqa.selenium.remote.CapabilityType.PAGE_LOAD_STRATEGY;

final class CommonCapabilitiesTest implements WithAssertions {
  private final AbstractDriverFactory driverFactory = new DummyDriverFactory();
  private final Proxy proxy = mock(Proxy.class);

  @Test
  void transferCapabilitiesFromConfiguration() {
    SelenideConfig config = new SelenideConfig();
    config.pageLoadStrategy("foo");
    Capabilities commonCapabilities = driverFactory.createCommonCapabilities(config, browser(config), proxy);
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_INSECURE_CERTS))).isTrue();
    assertThat(asBool(commonCapabilities.getCapability(ACCEPT_SSL_CERTS))).isTrue();
    assertThat(commonCapabilities.getCapability(PAGE_LOAD_STRATEGY)).isEqualTo(config.pageLoadStrategy());
  }

  private boolean asBool(Object raw) {
    if (raw != null) {
      if (raw instanceof String) {
        return Boolean.parseBoolean((String) raw);
      } else if (raw instanceof Boolean) {
        return (Boolean) raw;
      }
    }
    return false;
  }

  private Browser browser(SelenideConfig config) {
    return new Browser(config.browser(), config.headless());
  }

  private static class DummyDriverFactory extends AbstractDriverFactory {
    @Override
    public void setupWebdriverBinary() {
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public MutableCapabilities createCapabilities(@NotNull Config config, @NotNull Browser browser,
                                                  @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
      return new DesiredCapabilities();
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public WebDriver create(@NotNull Config config, @NotNull Browser browser, @Nullable Proxy proxy, @Nullable File browserDownloadsFolder) {
      return mock(WebDriver.class);
    }
  }
}
