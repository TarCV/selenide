package org.openqa.selenium.internal

expect interface HasIdentity {
    suspend fun getId(): String
}
