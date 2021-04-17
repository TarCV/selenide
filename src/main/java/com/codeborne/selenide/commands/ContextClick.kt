package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class ContextClick implements Command<SelenideElement> {
  @Override
  @Nonnull
  public SelenideElement execute(@NotNull SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    locator.driver().actions().contextClick(locator.findAndAssertElementIsInteractable()).perform();
    return proxy;
  }
}
