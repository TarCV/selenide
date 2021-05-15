package integration.customcommands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static integration.customcommands.MyFramework.quadrupleClickCounter;

class QuadrupleClick implements Command<MySelenideElement> {
  @Override
  @Nonnull
  public MySelenideElement execute(@NotNull SelenideElement proxy, @NotNull WebElementSource locator, @NotNull Object[] args) {
    quadrupleClickCounter.incrementAndGet();
    return (MySelenideElement) proxy;
  }
}
