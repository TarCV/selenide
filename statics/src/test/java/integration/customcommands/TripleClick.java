package integration.customcommands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static integration.customcommands.MyFramework.tripleClickCounter;

class TripleClick implements Command<MySelenideElement> {
  @Override
  @Nonnull
  public MySelenideElement execute(@NotNull SelenideElement proxy, @NotNull WebElementSource locator, @Nullable Object[] args) {
    tripleClickCounter.incrementAndGet();
    return (MySelenideElement) proxy;
  }
}
