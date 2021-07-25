package org.openqa.selenium.interactions

actual interface Locatable {
    actual suspend fun getCoordinates(): Coordinates
}
