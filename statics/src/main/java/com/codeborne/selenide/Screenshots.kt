package com.codeborne.selenide

import com.codeborne.selenide.impl.ScreenShotLaboratory
import com.codeborne.selenide.impl.Screenshot
import org.openqa.selenium.WebElement
import java.awt.image.BufferedImage
import java.io.File
import java.util.Optional
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
object Screenshots {
    var screenshots: ScreenShotLaboratory = ScreenShotLaboratory.getInstance()
    @CheckReturnValue
    fun saveScreenshotAndPageSource(): String {
        return screenshots.takeScreenshot(WebDriverRunner.driver()).summary()
    }

    @CheckReturnValue
    fun takeScreenShot(className: String, methodName: String): Screenshot {
        return screenshots.takeScreenShot(WebDriverRunner.driver(), className, methodName)
    }

    @CheckReturnValue
    @Deprecated("Use either {@link Selenide#screenshot(java.lang.String)} or {@link SelenideDriver#screenshot(java.lang.String)}")
    fun takeScreenShot(fileName: String): String? {
        return screenshots.takeScreenShot(WebDriverRunner.driver(), fileName)
    }

    /**
     * Take screenshot and return as a file
     * @return a temporary file, not guaranteed to be stored after tests complete.
     */
    @CheckReturnValue
    fun takeScreenShotAsFile(): File? {
        return screenshots.takeScreenShotAsFile(WebDriverRunner.driver())
    }

    /**
     * Take screenshot of the WebElement/SelenideElement
     * @return a temporary file, not guaranteed to be stored after tests complete.
     */
    @CheckReturnValue
    fun takeScreenShot(element: WebElement): File? {
        return screenshots.takeScreenshot(WebDriverRunner.driver(), element)
    }

    /**
     * Take screenshot of WebElement/SelenideElement in iframe
     * for partially visible WebElement/Selenide horizontal scroll bar will be present
     * @return a temporary file, not guaranteed to be stored after tests complete.
     */
    @CheckReturnValue
    fun takeScreenShot(iframe: WebElement, element: WebElement): File? {
        return screenshots.takeScreenshot(WebDriverRunner.driver(), iframe, element)
    }

    /**
     * Take screenshot of WebElement/SelenideElement in iframe
     * for partially visible WebElement/Selenide horizontal scroll bar will be present
     * @return buffered image
     */
    @CheckReturnValue
    fun takeScreenShotAsImage(iframe: WebElement, element: WebElement): BufferedImage? {
        return screenshots.takeScreenshotAsImage(WebDriverRunner.driver(), iframe, element)
    }

    /**
     * Take screenshot of the WebElement/SelenideElement
     * @return buffered image
     */
    @CheckReturnValue
    fun takeScreenShotAsImage(element: WebElement): BufferedImage? {
        return screenshots.takeScreenshotAsImage(WebDriverRunner.driver(), element)
    }

    fun startContext(className: String, methodName: String) {
        screenshots.startContext(className, methodName)
    }

    fun finishContext(): List<File> {
        return screenshots.finishContext()
    }

    /**
     * Get the last screenshot taken
     *
     * @return null if there were no any screenshots taken
     */
    @get:CheckReturnValue
    val lastScreenshot: File?
        get() = screenshots.lastScreenshot

    /**
     * Get the last screenshot taken in current thread
     *
     * @return [java.util.Optional] with screenshot of current thread,
     * or an empty Optional if there were no any screenshots taken.
     */
    @get:CheckReturnValue
    val lastThreadScreenshot: Optional<File>
        get() = screenshots.lastThreadScreenshot

    /**
     * Get the last screenshot taken in current `context` thread
     *
     * @return [java.util.Optional] with screenshot of current `context` thread,
     * or an empty Optional if there were no any screenshots taken.
     */
    @get:CheckReturnValue
    val lastContextScreenshot: Optional<File>
        get() = screenshots.lastContextScreenshot
}
