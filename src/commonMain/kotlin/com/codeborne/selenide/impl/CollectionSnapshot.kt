package com.codeborne.selenide.impl

import co.touchlab.stately.isolate.IsolateState
import com.codeborne.selenide.Driver
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.openqa.selenium.WebElement

// TODO: this class had toString with side-effects in Java
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
    override fun description(): String {
        return alias.getOrElse {
            "${originalCollection.description()}.snapshot()"
        }
    }
    override fun driver(): Driver {
        return originalCollection.driver()
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }
}
