package com.codeborne.selenide.proxy

import java.net.InetAddress
import java.net.UnknownHostException
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
open class InetAddressResolver {
    @CheckReturnValue
    open fun getInetAddressByName(hostname: String): InetAddress {
        return try {
            InetAddress.getByName(hostname)
        } catch (e: UnknownHostException) {
            throw IllegalArgumentException(e)
        }
    }
}
