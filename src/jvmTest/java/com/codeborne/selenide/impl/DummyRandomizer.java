package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class DummyRandomizer extends Randomizer {
  private final String text;

  public DummyRandomizer(String text) {
    this.text = text + "-" +  UUID.randomUUID();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String text() {
    return text;
  }
}
