package com.codeborne.selenide.drivercommands


internal class BasicAuthUrl {
    fun appendBasicAuthToURL(url: String, domain: String, login: String, password: String): String {
        var domain = domain
        var login = login
        var password = password
        if (domain.isNotEmpty()) domain += "%5C"
        if (login.isNotEmpty()) login += ":"
        if (password.isNotEmpty()) password += "@"
        val index = url.indexOf("://") + 3
        return if (index < 3) domain + login + password + url else url.substring(0, index - 3) + "://" +
          domain +
          login +
          password +
          url.substring(index)
    }
}
