package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Browsers.CHROME;
import static org.assertj.core.api.Assertions.assertThat;

final class BrowserTest {
  @Test
  void browserNameIsCaseInsensitive() {
    assertThat(new Browser(CHROME, false).isChrome()).isTrue();
    assertThat(new Browser("chrome", false).isChrome()).isTrue();
    assertThat(new Browser("cHromE", false).isChrome()).isTrue();
    assertThat(new Browser("firefox", false).isChrome()).isFalse();
  }

  @Test
  void mostBrowsersSupportInsecureCerts() {
    assertThat(new Browser(CHROME, false).supportsInsecureCerts()).isTrue();
  }
}
