package support.logging

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

expect class Level {
    fun intValue(): Int

    companion object {
        @JvmStatic
        @JvmField
        val ALL: Level
    }
}
