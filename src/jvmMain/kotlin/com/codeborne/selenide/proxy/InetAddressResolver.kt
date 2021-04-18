package com.codeborne.selenide.proxy

import java.net.InetAddress
import java.net.UnknownHostException

open class InetAddressResolver {
    open fun getInetAddressByName(hostname: String): InetAddress {
        return try {
            InetAddress.getByName(hostname)
        } catch (e: UnknownHostException) {
            throw IllegalArgumentException(e)
        }
    }
}
