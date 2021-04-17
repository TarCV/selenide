package com.codeborne.selenide.proxy;

import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.InetAddress;
import java.net.UnknownHostException;

class InetAddressResolverStub extends InetAddressResolver {
  @Override
  @CheckReturnValue
  @Nonnull
  public InetAddress getInetAddressByName(@NotNull String hostname) {
    try {
      return InetAddress.getByAddress(hostname, new byte[4]);
    }
    catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }
}
