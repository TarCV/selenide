package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import org.assertj.core.api.Assertions.assertThat;

class PluginsTest {
  @Test
  fun loadsDefaultImplementationsFromMetaInf() {
    assertThat(Plugins.elementDescriber).isInstanceOf(SelenideElementDescriber::class.java)
// TODO:    assertThat(injectJvm(Photographer::class)).isInstanceOf(WebdriverPhotographer.class)
  }
}
