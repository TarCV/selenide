package com.codeborne.selenide

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.IOException

class DefaultClipboard(private val driver: Driver) : Clipboard {
    override var text: String
        get() {
            assertRemoteState()
            return try {
                Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor).toString()
            } catch (e: UnsupportedFlavorException) {
                throw IllegalStateException("Can't get clipboard data! " + e.message, e)
            } catch (e: IOException) {
                throw IllegalStateException("Can't get clipboard data! " + e.message, e)
            }
        }
        set(text) {
            assertRemoteState()
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), StringSelection(text))
        }

    private fun assertRemoteState() {
        check(driver.config().remote() == null) { "Remote driver url detected! Please use remote clipboard." }
    }
}
