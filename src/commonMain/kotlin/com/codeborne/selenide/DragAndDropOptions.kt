package com.codeborne.selenide

import kotlin.jvm.JvmStatic


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
        return "method: ${method}"
    }

    companion object {
        @JvmStatic
        fun usingJavaScript(): DragAndDropOptions {
            return DragAndDropOptions(DragAndDropMethod.JS)
        }

        @JvmStatic
        fun usingActions(): DragAndDropOptions {
            return DragAndDropOptions(DragAndDropMethod.ACTIONS)
        }
    }
}
