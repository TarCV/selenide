package com.codeborne.selenide

import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class DragAndDropOptions(val method: DragAndDropMethod) {

    enum class DragAndDropMethod {
        /**
         * Executing drag and drop via Selenium Actions
         */
        ACTIONS,

        /**
         * Executing drag and drop via JS script
         */
        JS
    }

    override fun toString(): String {
        return String.format("method: %s", method)
    }

    companion object {
        @JvmStatic
        fun usingJavaScript(): DragAndDropOptions {
            return DragAndDropOptions(DragAndDropMethod.JS)
        }

        fun usingActions(): DragAndDropOptions {
            return DragAndDropOptions(DragAndDropMethod.ACTIONS)
        }
    }
}
