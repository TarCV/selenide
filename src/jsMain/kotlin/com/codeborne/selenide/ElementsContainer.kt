package com.codeborne.selenide


abstract class ElementsContainer {
    @get:Deprecated(
        """I rather think that this method is not needed.
    You are expected to find elements INSIDE this container, not the container itself."""
    )

    // TODO: why this was NonNull in Java?
    val self: SelenideElement? = null

}
