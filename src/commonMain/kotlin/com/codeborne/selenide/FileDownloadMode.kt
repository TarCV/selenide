package com.codeborne.selenide

enum class FileDownloadMode {
    /**
     * Download files via direct http request.
     * Works only for `<a href></a>` elements.
     * Sends GET request to "href" with all cookies from current browser session.
     */
    HTTPGET,

    /**
     * Download files via selenide embedded proxy server.
     * Works for any elements (e.g. form submission).
     * Doesn't work if you are using custom webdriver without selenide proxy server.
     */
    PROXY,

    /**
     * Download files to a local "downloads" folder
     */
    FOLDER
}
