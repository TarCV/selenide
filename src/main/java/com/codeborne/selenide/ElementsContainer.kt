package com.codeborne.selenide

import com.google.errorprone.annotations.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
abstract class ElementsContainer {
    @get:Deprecated(
        """I rather think that this method is not needed.
    You are expected to find elements INSIDE this container, not the container itself."""
    )

    // TODO: why this was NonNull in Java?
    @get:CheckReturnValue
    val self: SelenideElement? = null

}
