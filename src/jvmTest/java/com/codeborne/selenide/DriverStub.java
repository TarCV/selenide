package com.codeborne.selenide;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.io.File;
import java.security.Provider;
import java.util.Objects;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;

/**
 * A dummy `Driver` implementation used in tests.
 */
@ParametersAreNonnullByDefault
public class DriverStub implements Driver {
  private final Config config;
  private final Browser browser;
  private final WebDriver webDriver;
  @Nullable private final Supplier<File> tempDir;

  public DriverStub() {
    this("zopera");
  }

  public DriverStub(String browser) {
    this(null, new SelenideConfig(), new Browser(browser, false), mock(WebDriver.class), null);
  }

  public DriverStub(@Nullable Supplier<File> tempDir, Config config, Browser browser,
                    WebDriver webDriver,
                    @Nullable Object proxy) {
    this.config = config;
    this.browser = browser;
    this.webDriver = webDriver;

    if (tempDir == null) {
      this.tempDir = null;
    } else {
      this.tempDir = tempDir;
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Config config() {
    return config;
  }

  @Override
  @CheckReturnValue
  public boolean hasWebDriverStarted() {
    return webDriver != null;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Browser browser() {
    return browser;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver getWebDriver() {
    return webDriver;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver getGetAndCheckWebDriver() {
    return webDriver;
  }

  @Override
  @CheckReturnValue
  @Nullable
  public DownloadsFolder browserDownloadsFolder() {
    Objects.requireNonNull(tempDir);
    return new SharedDownloadsFolder(tempDir.get() + "/build/downloads/45");
  }

  @Override
  public void close() {
    webDriver.close();
  }

  @Override
  @CheckReturnValue
  public boolean supportsJavascript() {
    return hasWebDriverStarted() && webDriver instanceof JavascriptExecutor;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T executeJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) webDriver).executeScript(jsCode, arguments);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T executeAsyncJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) webDriver).executeAsyncScript(jsCode, arguments);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String getUserAgent() {
    return "zhopera";
  }

  @Override
  @CheckReturnValue
  @Nonnull  public SelenideTargetLocator switchTo() {
    return new SelenideTargetLocator(this);
  }

  @Override
  @CheckReturnValue
  @Nonnull  public Actions actions() {
    return new Actions(getWebDriver());
  }
}
