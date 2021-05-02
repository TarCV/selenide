package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import com.codeborne.selenide.impl.Plugins.injectJvm;
import org.assertj.core.api.Assertions.assertThat;

class PluginsTest {
  @Test
  fun loadsDefaultImplementationsFromMetaInf() {
    assertThat(injectJvm(ElementDescriber::class)).isInstanceOf(SelenideElementDescriber::class.java)
// TODO:    assertThat(injectJvm(Photographer::class)).isInstanceOf(WebdriverPhotographer.class)
  }
}
