package org.openqa.selenium.internal

actual interface HasIdentity {
    actual suspend fun getId(): String
}
