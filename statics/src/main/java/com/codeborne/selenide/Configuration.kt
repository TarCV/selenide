package com.codeborne.selenide

import org.openqa.selenium.Capabilities
import org.openqa.selenium.MutableCapabilities

/**
 * Configuration settings for Selenide default browser
 * <br></br>
 * This class is designed so that every setting can be set either via system property or programmatically.
 * <br></br>
 * Please note that all fields are static, meaning that
 * every change will immediately reflect in all threads (if you run tests in parallel).
 *
 *
 *
 * These system properties can be additonally used having effect on every new created browser in test.
 * For example as -D&lt;property&gt;=&lt;value&gt; in command-line
 *
 *
 *
 * **chromeoptions.args** - Sets the arguments for chrome options, parameters are comma separated
 * If comma is a part of the value, use double quotes around the argument
 * Non-official list of parameters can be found at https://peter.sh/experiments/chromium-command-line-switches/
 *
 * Example: --no-sandbox,--disable-3d-apis,"--user-agent=Firefox 45, Mozilla"
 *
 *
 *
 * **chromeoptions.prefs** - Sets the preferences for chrome options, which are comma separated
 * keyX=valueX preferences. If comma is a part of the value, use double quotes around the preference
 * List of preferences can be found at
 * https://chromium.googlesource.com/chromium/src/+/master/chrome/common/pref_names.cc
 *
 * Example: homepage=http://google.com,"intl.allowed_languages=en,ru,es"
 *
 */
object Configuration {
    private val defaults = SelenideConfig()

    /**
     * Base url for open() function calls
     * Can be configured either programmatically or by system property "-Dselenide.baseUrl=http://myhost".
     * <br></br>
     * Default value: http://localhost:8080
     */
    var baseUrl = defaults.baseUrl()

    /**
     * Timeout in milliseconds to fail the test, if conditions still not met
     * Can be configured either programmatically or by system property "-Dselenide.timeout=10000"
     * <br></br>
     * Default value: 4000 (milliseconds)
     */
    var timeout = defaults.timeout()

    /**
     * Interval in milliseconds, when checking if a single element or collection elements are appeared
     * Can be configured either programmatically or by system property "-Dselenide.pollingInterval=50"
     * <br></br>
     * Default value: 200 (milliseconds)
     */
    var pollingInterval = defaults.pollingInterval()

    /**
     * If holdBrowserOpen is true, browser window stays open after running tests. It may be useful for debugging.
     * Can be configured either programmatically or by system property "-Dselenide.holdBrowserOpen=true".
     * <br></br>
     * Default value: false.
     */
    var holdBrowserOpen = defaults.holdBrowserOpen()

    /**
     * Should Selenide re-spawn browser if it's disappeared (hangs, broken, unexpectedly closed).
     * <br></br>
     * Can be configured either programmatically or by system property "-Dselenide.reopenBrowserOnFail=false".
     * <br></br>
     * Set this property to false if you want to disable automatic re-spawning the browser.
     * <br></br>
     * Default value: true
     */
    var reopenBrowserOnFail = defaults.reopenBrowserOnFail()

    /**
     * Which browser to use.
     * Can be configured either programmatically or by system property "-Dselenide.browser=ie".
     * Supported values: "chrome", "firefox", "legacy_firefox" (upto ESR 52), "ie", "opera", "edge"
     * <br></br>
     * Default value: "chrome"
     */
    var browser: String = defaults.browser()

    /**
     * Which browser version to use (for Internet Explorer).
     * Can be configured either programmatically or by system property "-Dselenide.browserVersion=8".
     * <br></br>
     * Default value: none
     */
    var browserVersion: String? = defaults.browserVersion()

    /**
     * URL of remote web driver (in case of using Selenium Grid).
     * Can be configured either programmatically or by system property "-Dselenide.remote=http://localhost:5678/wd/hub".
     * <br></br>
     * Default value: null (Grid is not used).
     */
    var remote: String? = defaults.remote()

    /**
     * The browser window size.
     * Can be configured either programmatically or by system property "-Dselenide.browserSize=1024x768".
     * <br></br>
     * Default value: 1366x768
     */
    var browserSize: String = defaults.browserSize()

    /**
     * The browser window position on screen.
     * Can be configured either programmatically or by system property "-Dselenide.browserPosition=10x10".
     * <br></br>
     * Default value: none
     */
    var browserPosition: String? = defaults.browserPosition()

    /**
     * The browser window is maximized when started.
     * Can be configured either programmatically or by system property "-Dselenide.startMaximized=true".
     * <br></br>
     * Default value: false
     */
    var startMaximized = defaults.startMaximized()

    /**
     * Browser capabilities.
     * Warning: this capabilities will override capabilities were set by system properties.
     * <br></br>
     * Default value: DesiredCapabilities::new
     */
    var browserCapabilities: MutableCapabilities = defaults.browserCapabilities()

    /**
     * Should webdriver wait until page is completely loaded.
     * Possible values: "none", "normal" and "eager".
     * <br></br>
     * Can be configured either programmatically or by system property "-Dselenide.pageLoadStrategy=eager".
     * Default value: "normal".
     * <br></br>
     * - `normal`: return after the load event fires on the new page (it's default in Selenium webdriver);
     * - `eager`: return after DOMContentLoaded fires;
     * - `none`: return immediately
     * <br></br>
     * In some cases `eager` can bring performance boosts for the slow tests.
     * Though, we left default value `normal` because we afraid to break users' existing tests.
     * <br></br>
     * See https://w3c.github.io/webdriver/webdriver-spec.html#dfn-page-loading-strategy
     *
     * @since 3.5
     */
    var pageLoadStrategy: String = defaults.pageLoadStrategy()

    /**
     * Timeout for loading a web page (in milliseconds).
     * Default timeout in Selenium WebDriver is 300 seconds (which is incredibly long).
     * Selenide default is 30 seconds.
     *
     * @since 5.15.0
     */
    var pageLoadTimeout = defaults.pageLoadTimeout()

    /**
     * ATTENTION! Automatic WebDriver waiting after click isn't working in case of using this feature.
     * Use clicking via JavaScript instead common element clicking.
     * This solution may be helpful for testing in Internet Explorer.
     * Can be configured either programmatically or by system property "-Dselenide.clickViaJs=true".
     * <br></br>
     * Default value: false
     */
    var clickViaJs = defaults.clickViaJs()

    /**
     * Defines if Selenide takes screenshots on failing tests.
     * Can be configured either programmatically or by system property "-Dselenide.screenshots=false".
     * <br></br>
     * Default value: true
     */
    var screenshots = defaults.screenshots()

    /**
     * Defines if Selenide saves page source on failing tests.
     * Can be configured either programmatically or by system property "-Dselenide.savePageSource=false".
     * <br></br>
     * Default value: true
     */
    var savePageSource = defaults.savePageSource()

    /**
     * Folder to store screenshots to.
     * Can be configured either programmatically or by system property "-Dselenide.reportsFolder=test-result/reports".
     * <br></br>
     * Default value: "build/reports/tests" (this is default for Gradle projects)
     */
    var reportsFolder: String = defaults.reportsFolder()

    /**
     * Folder to store downloaded files to.
     * Can be configured either programmatically or by system property "-Dselenide.downloadsFolder=test-result/downloads".
     * <br></br>
     * Default value: "build/downloads" (this is default for Gradle projects)
     */
    var downloadsFolder: String = defaults.downloadsFolder()

    /**
     * Optional: URL of CI server where reports are published to.
     * In case of Jenkins, it is "BUILD_URL/artifact" by default.
     * <br></br>
     * Can be configured either programmatically or by system property "-Dselenide.reportsUrl=http://jenkins-host/reports".
     * <br></br>
     * If it's given, names of screenshots are printed as
     * "http://ci.mycompany.com/job/my-job/446/artifact/build/reports/tests/my_test.png" - it's useful to analyze test
     * failures in CI server.
     */
    var reportsUrl: String? = defaults.reportsUrl()

    /**
     * If set to true, sets value by javascript instead of using Selenium built-in "sendKey" function
     * (that is quite slow because it sends every character separately).
     * <br></br>
     * Tested on Codeborne projects - works well, speed up ~30%.
     * Some people reported 150% speedup (because sending characters one-by-one was especially
     * slow via network to Selenium Grid on cloud).
     * <br></br>
     * https://github.com/selenide/selenide/issues/135
     * Can be configured either programmatically or by system property "-Dselenide.fastSetValue=true".
     * <br></br>
     * Default value: false
     */
    var fastSetValue = defaults.fastSetValue()

    /**
     * If set to true, 'setValue' and 'val' methods of SelenideElement can work as 'selectOptionByValue', 'selectRadio'
     * depending on the real control type, defined by element's tag.
     * <br></br>
     * Will decrease performance of setValue, make it slower, but will also make tests implementation more "business oriented".
     * With this property being set to true, tests may no longer be dependent on actual control implementation in html and
     * be more abstract.
     * <br></br>
     * https://github.com/selenide/selenide/issues/508
     * Can be configured either programmatically or by system property "-Dselenide.versatileSetValue=true".
     * <br></br>
     * Default value: false
     */
    var versatileSetValue = defaults.versatileSetValue()

    /**
     *
     * Choose how Selenide should retrieve web elements: using default CSS or Sizzle (CSS3).
     * <br></br>
     *
     *
     * Can be configured either programmatically or by system property "-Dselenide.selectorMode=Sizzle".
     *
     * <br></br>
     * Possible values: "CSS" or "Sizzle"
     * <br></br>
     * Default value: CSS
     *
     * @see SelectorMode
     */
    var selectorMode: SelectorMode = defaults.selectorMode()

    /**
     *
     * Assertion mode
     *
     *
     * Can be configured either programmatically or by system property "-Dselenide.assertionMode=SOFT".
     *
     * <br></br>
     * Possible values: "STRICT" or "SOFT"
     * <br></br>
     * Default value: STRICT
     *
     * @see AssertionMode
     */
    var assertionMode: AssertionMode = defaults.assertionMode()

    /**
     * Defines if files are downloaded via direct HTTP or vie selenide embedded proxy server
     * Can be configured either programmatically or by system property "-Dselenide.fileDownload=PROXY"
     * <br></br>
     * Default: HTTPGET
     */
    var fileDownload: FileDownloadMode = defaults.fileDownload()

    /**
     * If Selenide should run browser through its own proxy server.
     * It allows some additional features which are not possible with plain Selenium.
     * But it's not enabled by default because sometimes it would not work (more exactly, if tests and browser and
     * executed on different machines, and "test machine" is not accessible from "browser machine"). If it's not your
     * case, I recommend to enable proxy.
     * Can be configured either programmatically or by system property "-Dselenide.proxyEnabled=true"
     * <br></br>
     * Default: false
     */
    var proxyEnabled = defaults.proxyEnabled()

    /**
     * Host of Selenide proxy server.
     * Used only if proxyEnabled == true.
     * Can be configured either programmatically or by system property "-Dselenide.proxyHost=127.0.0.1"
     * <br></br>
     * Default: empty (meaning that Selenide will detect current machine's ip/hostname automatically)
     *
     * @see com.browserup.bup.client.ClientUtil.getConnectableAddress
     */
    var proxyHost: String = defaults.proxyHost()

    /**
     * Port of Selenide proxy server.
     * Used only if proxyEnabled == true.
     * Can be configured either programmatically or by system property "-Dselenide.proxyPort=8888"
     * <br></br>
     * Default: 0 (meaning that Selenide will choose a random free port on current machine)
     */
    var proxyPort = defaults.proxyPort()

    /**
     * Controls Selenide and WebDriverManager integration.
     * When integration is enabled you don't need to download and setup any browser driver executables.
     * See https://github.com/bonigarcia/webdrivermanager for WebDriverManager configuration details.
     * Can be configured either programmatically or by system property "-Dselenide.driverManagerEnabled=false"
     * <br></br>
     *
     * Default: true
     */
    var driverManagerEnabled = defaults.driverManagerEnabled()

    /**
     *
     *
     * Whether webdriver logs should be enabled.
     *
     *
     *
     *
     * These logs may be useful for debugging some webdriver issues.
     * But in most cases they are not needed (and can take quite a lot of disk space),
     * that's why don't enable them by default.
     *
     *
     * Default: false
     * @since 5.18.0
     */
    var webdriverLogsEnabled = defaults.webdriverLogsEnabled()

    /**
     * Enables the ability to run the browser in headless mode.
     * Works only for Chrome(59+) and Firefox(56+).
     * Can be configured either programmatically or by system property "-Dselenide.headless=true"
     * <br></br>
     * Default: false
     */
    var headless = defaults.headless()

    /**
     * Sets the path to browser executable.
     * Works only for Chrome, Firefox and Opera.
     * Can be configured either programmatically or by system property "-Dselenide.browserBinary=/path/to/binary"
     */
    var browserBinary: String = defaults.browserBinary()
}
