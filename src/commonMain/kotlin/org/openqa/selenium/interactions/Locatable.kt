package org.openqa.selenium.interactions

expect interface Locatable {
    suspend fun getCoordinates(): Coordinates
}
