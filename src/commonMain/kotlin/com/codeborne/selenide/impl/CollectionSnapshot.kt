package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.openqa.selenium.WebElement

class CollectionSnapshot(private val originalCollection: CollectionSource) : CollectionSource {
    private val _elementsSnapshot = GlobalScope.async(Dispatchers.Unconfined, start = CoroutineStart.LAZY) {
        ArrayList(originalCollection.getElements())
    }

    private suspend fun getElementsSnapshot(): List<org.openqa.selenium.WebElement> {
        return _elementsSnapshot.await()
    }

    private var alias = Alias.NONE
    override suspend fun getElements(): List<org.openqa.selenium.WebElement> {
        return getElementsSnapshot()
    }
    override suspend fun getElement(index: Int): org.openqa.selenium.WebElement {
        return getElementsSnapshot()[index]
    }
    override suspend fun description(): String {
        return alias.getOrElseAsync {
            "${originalCollection.description()}.snapshot(${getElementsSnapshot()}.size elements)"
        }
    }
    override fun driver(): Driver {
        return originalCollection.driver()
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }
}
