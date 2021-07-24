package integration

import assertk.Assert
import assertk.all
import assertk.assertThat
import assertk.assertions.cause
import assertk.assertions.hasClass
import assertk.assertions.hasMessage
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import assertk.assertions.message
import assertk.assertions.messageContains
import assertk.assertions.startsWith
import assertk.assertions.support.appendName
import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.CollectionCondition.Companion.allMatch
import com.codeborne.selenide.CollectionCondition.Companion.anyMatch
import com.codeborne.selenide.CollectionCondition.Companion.containExactTextsCaseSensitive
import com.codeborne.selenide.CollectionCondition.Companion.exactTexts
import com.codeborne.selenide.CollectionCondition.Companion.itemWithText
import com.codeborne.selenide.CollectionCondition.Companion.noneMatch
import com.codeborne.selenide.CollectionCondition.Companion.size
import com.codeborne.selenide.CollectionCondition.Companion.sizeGreaterThan
import com.codeborne.selenide.CollectionCondition.Companion.sizeGreaterThanOrEqual
import com.codeborne.selenide.CollectionCondition.Companion.sizeLessThan
import com.codeborne.selenide.CollectionCondition.Companion.sizeLessThanOrEqual
import com.codeborne.selenide.CollectionCondition.Companion.sizeNotEqual
import com.codeborne.selenide.CollectionCondition.Companion.texts
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.and
import com.codeborne.selenide.Condition.Companion.cssClass
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.Condition.Companion.value
import com.codeborne.selenide.Selectors.byText
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.DoesNotContainTextsError
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.ElementWithTextNotFound
import com.codeborne.selenide.ex.ListSizeMismatch
import com.codeborne.selenide.ex.MatcherError
import com.codeborne.selenide.ex.TextsMismatch
import com.codeborne.selenide.ex.TextsSizeMismatch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.InvalidSelectorException
import org.openqa.selenium.NoSuchElementException
import java.util.Arrays
import java.util.stream.Collectors
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalFileSystem
internal class CollectionMethodsTest : ITest() {
    @BeforeEach
    fun openTestPageWithJQuery() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun useTwoDollarsToGetListOfElements() = runBlockingTest {
        `$$`("#radioButtons input").shouldHave(size(4))
        `$$`(By.cssSelector("#radioButtons input")).shouldHave(size(4))
        `$`("#radioButtons").`$$`("input").shouldHave(size(4))
        `$`("#radioButtons").`$$`(By.tagName("input")).shouldHave(size(4))
        `$`("#radioButtons").findAll("input").shouldHave(size(4))
        `$`("#radioButtons").findAll(By.tagName("input")).shouldHave(size(4))
    }

    @Test
    fun invalidSelector() = runBlockingTest {
        assertThat { `$$`(By.xpath("//xxx[@'")).shouldHave(size(0)) }
            .isFailure()
            .isInstanceOf(InvalidSelectorException::class)
    }

    @Test
    fun canUseSizeMethod() = runBlockingTest {
        Assertions.assertThat(`$$`(By.name("domain")).getSize())
            .isEqualTo(1)
        Assertions.assertThat(`$$`("#theHiddenElement").getSize())
            .isEqualTo(1)
        Assertions.assertThat(`$$`("#radioButtons input").getSize())
            .isEqualTo(4)
        Assertions.assertThat(`$$`(By.xpath("//select[@name='domain']/option")).getSize())
            .isEqualTo(4)
        Assertions.assertThat(`$$`(By.name("non-existing-element")).getSize())
            .isEqualTo(0)
    }

    @Test
    fun canCheckIfCollectionIsEmpty() = runBlockingTest {
        `$$`(By.name("#dynamic-content-container span")).shouldBe(CollectionCondition.empty)
        `$$`(By.name("non-existing-element")).shouldBe(CollectionCondition.empty)
        `$$`(byText("Loading...")).shouldBe(CollectionCondition.empty)
    }

    @Test
    fun canCheckIfCollectionIsEmptyForNonExistingParent() = runBlockingTest {
        `$$`("not-existing-locator").first().`$$`("#multirowTable")
            .shouldHaveSize(0)
            .shouldBe(CollectionCondition.empty)
            .shouldBe(size(0))
            .shouldBe(sizeGreaterThan(-1))
            .shouldBe(sizeGreaterThanOrEqual(0))
            .shouldBe(sizeNotEqual(1))
            .shouldBe(sizeLessThan(1))
            .shouldBe(sizeLessThanOrEqual(0))
        assertThat(`$$`("not-existing-locator").last().`$$`("#multirowTable").isEmpty()).isTrue()
    }

    @Test
    fun canCheckSizeOfCollection() = runBlockingTest {
        withLongTimeout {
            `$$`(By.name("domain")).shouldHaveSize(1)
            `$$`("#theHiddenElement").shouldHaveSize(1)
            `$$`("#radioButtons input").shouldHaveSize(4)
            `$$`(By.xpath("//select[@name='domain']/option")).shouldHaveSize(4)
            `$$`(By.name("non-existing-element")).shouldHaveSize(0)
            `$$`("#dynamic-content-container span").shouldHave(size(2))
        }
    }

    @Test
    fun shouldWaitUntilCollectionGetsExpectedSize() = runBlockingTest {
        withLongTimeout {
            val spans = `$$`("#dynamic-content-container span")
            spans.shouldHave(size(2)) // appears after 2 seconds
            Assertions.assertThat(spans.getSize()).isEqualTo(2)
            Assertions.assertThat<Any>(spans.texts()).isEqualTo(Arrays.asList("dynamic content", "dynamic content2"))
        }
    }

    @Test
    fun canCheckThatElementsHaveCorrectTexts() = runBlockingTest {
        withLongTimeout {
            `$$`("#dynamic-content-container span").shouldHave(
                texts("dynamic content", "dynamic content2"),
                texts("mic cont", "content2"),
                exactTexts(Arrays.asList("dynamic content", "dynamic content2"))
            )
        }
    }

    @Test
    fun ignoresWhitespacesInTexts() = runBlockingTest {
        withLongTimeout {
            `$$`("#dynamic-content-container span").shouldHave(
                texts("   dynamic \ncontent ", "dynamic \t\t\tcontent2\t\t\r\n"),
                exactTexts("dynamic \t\n content\n\r", "    dynamic content2      ")
            )
        }
    }

    @Test
    fun canCheckThatElementsHaveExactlyCorrectTexts() = runBlockingTest {
        withLongTimeout {
            assertThat {
                `$$`("#dynamic-content-container span").shouldHave(
                    exactTexts(
                        "content",
                        "content2"
                    )
                )
            }
                .isFailure()
                .isInstanceOf(TextsMismatch::class.java)
        }
    }

    @Test
    fun textsCheckThrowsElementNotFound() = runBlockingTest {
        assertThat { `$$`(".non-existing-elements").shouldHave(texts("content1", "content2")) }
            .isFailure()
            .isInstanceOf(ElementNotFound::class.java)
    }

    @Test
    fun exactTextsCheckThrowsElementNotFound() = runBlockingTest {
        assertThat { `$$`(".non-existing-elements").shouldHave(exactTexts("content1", "content2")) }
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                message()
                    .isNotNull()
                    .startsWith("Element not found {.non-existing-elements}")
            }
    }

    @Test
    fun textsCheckThrowsTextsSizeMismatch() = runBlockingTest {
        withLongTimeout {
            assertThat {
                `$$`("#dynamic-content-container span")
                    .shouldHave(texts("static-content1", "static-content2", "dynamic-content1"))
            }
                .isFailure()
                .isInstanceOf(TextsSizeMismatch::class.java)
        }
    }

    @Test
    fun textCheckThrowsTextsMismatch() = runBlockingTest {
        withLongTimeout {
            assertThat {
                `$$`("#dynamic-content-container span").shouldHave(
                    texts(
                        "static-content1",
                        "static-content2"
                    )
                )
            }
                .isFailure()
                .isInstanceOf(TextsMismatch::class.java)
        }
    }

    @Test
    fun failsFast_ifNoExpectedTextsAreGiven() = runBlockingTest {
        assertThat { `$$`("#dynamic-content-container span").shouldHave(texts()) }
            .isFailure()
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun userCanFilterOutMatchingElements() = runBlockingTest {
        `$$`("#multirowTable tr").shouldHaveSize(2)
        `$$`("#multirowTable tr").filterBy(text("Norris")).shouldHaveSize(1)
        `$$`("#multirowTable tr").filterBy(cssClass("inexisting")).shouldHaveSize(0)
    }

    @Test
    fun userCanExcludeMatchingElements() = runBlockingTest {
        `$$`("#multirowTable tr").shouldHaveSize(2)
        `$$`("#multirowTable tr").excludeWith(text("Chack")).shouldHaveSize(0)
        `$$`("#multirowTable tr").excludeWith(cssClass("inexisting")).shouldHaveSize(2)
    }

    @Test
    fun errorMessageShouldShowFullAndConditionDescription() = runBlockingTest {
        val filteredRows = `$$`("#multirowTable tr")
            .filterBy(and("condition name", text("Chack"), text("Baskerville")))
        assertThat { filteredRows.shouldHave(size(0)) }
            .isFailure()
            .messageContains("collection: #multirowTable tr.filter(condition name: text 'Chack' and text 'Baskerville'")
    }

    @Test
    fun errorMessageShouldShow_whichElementInChainWasNotFound() = runBlockingTest {
        assertThat {
            `$$`("#multirowTable").findBy(text("INVALID-TEXT"))
                .findAll("valid-selector")
                .shouldHave(texts("foo bar"))
        }
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                messageContains("Element not found {#multirowTable.findBy(text 'INVALID-TEXT')}")
            }
    }

    @Test
    fun userCanFindMatchingElementFromList() = runBlockingTest {
        `$$`("#multirowTable tr").findBy(text("Norris")).shouldHave(text("Norris"))
    }

    @Test
    fun findWaitsUntilElementMatches() = runBlockingTest {
        withLongTimeout {
            `$$`("#dynamic-content-container span").findBy(text("dynamic content2")).shouldBe(Condition.visible)
            `$$`("#dynamic-content-container span").findBy(text("unexisting")).shouldNot(Condition.exist)
        }
    }

    @Test
    fun collectionMethodsCanBeChained() = runBlockingTest {
        `$$`("#multirowTable tr").shouldHave(size(2))
            .filterBy(text("Norris")).shouldHave(size(1))
    }

    @Test
    fun shouldMethodsCanCheckMultipleConditions() = runBlockingTest {
        `$$`("#multirowTable tr td").shouldHave(
            size(4),
            texts(Arrays.asList("Chack", "Norris", "Chack", "L'a Baskerville"))
        )
    }

    @Test
    fun canGetCollectionElementByIndex() = runBlockingTest {
        `$$`("#radioButtons input").get(0).shouldHave(value("master"))
        `$$`("#radioButtons input").get(1).shouldHave(value("margarita"))
        `$$`("#radioButtons input").get(2).shouldHave(value("cat"))
        `$$`("#radioButtons input").get(3).shouldHave(value("woland"))
    }

    @Test
    fun canGetCollectionFirstElement() = runBlockingTest {
        `$$`("#radioButtons input").first().shouldHave(value("master"))
    }

    @Test
    fun canGetCollectionLastElement() = runBlockingTest {
        `$$`("#radioButtons input").last().shouldHave(value("woland"))
    }

    @Test
    fun canFindElementsByMultipleSelectors() = runBlockingTest {
        `$$`(".first_row").shouldHave(size(1))
        `$$`(".second_row").shouldHave(size(1))
        `$$`(".first_row,.second_row").shouldHave(size(2))
    }

    @Test
    fun canIterateCollection_withIterator() = runBlockingTest {
        val it = `$$`("[name=domain] option").asFlow().toList().listIterator()
        Assertions.assertThat(it.hasNext())
            .isTrue
        it.next().shouldHave(text("@livemail.ru"))
        Assertions.assertThat(it.hasNext())
            .isTrue
        it.next().shouldHave(text("@myrambler.ru"))
        Assertions.assertThat(it.hasNext())
            .isTrue
        it.next().shouldHave(text("@rusmail.ru"))
        Assertions.assertThat(it.hasNext())
            .isTrue
        it.next().shouldHave(text("@мыло.ру"))
        Assertions.assertThat(it.hasNext())
            .isFalse
    }

    @Test
    fun canIterateCollection_withListIterator() = runBlockingTest {
        val it: ListIterator<SelenideElement> = `$$`("[name=domain] option")
            .asReversedFlow(3)
            .toList().listIterator()
        Assertions.assertThat(it.hasNext())
            .isTrue
        Assertions.assertThat(it.hasPrevious())
            .isFalse
        it.next().shouldHave(text("@rusmail.ru"))
        Assertions.assertThat(it.hasNext())
            .isTrue
        it.next().shouldHave(text("@myrambler.ru"))
        Assertions.assertThat(it.hasNext())
            .isTrue
        it.next().shouldHave(text("@livemail.ru"))
        Assertions.assertThat(it.hasNext())
            .isFalse
        it.previous().shouldHave(text("@livemail.ru"))
        Assertions.assertThat(it.hasNext())
            .isTrue
    }

    @Test
    fun canGetFirstNElements() = runBlockingTest {
        val collection = `$$x`("//select[@name='domain']/option")
        collection.first(2).shouldHaveSize(2)
        collection.first(10).shouldHaveSize(collection.getSize())
        val regularSublist: List<String> = `$$x`("//select[@name='domain']/option").asFlow()
            .map { obj -> obj.text }
            .toList()
            .subList(0, 2)
        val selenideSublist: List<String> = collection.first(2).asFlow()
            .map { obj -> obj.text }
            .toList()
        Assertions.assertThat(selenideSublist).isEqualTo(regularSublist)
    }

    @Test
    fun canGetElementByIndex_fromFirstNElements() = runBlockingTest {
        val collection = `$$x`("//select[@name='domain']/option").first(3).shouldHave(size(3))
        collection.get(0).shouldHave(text("@livemail.ru"))
        collection.get(1).shouldHave(text("@myrambler.ru"))
        collection.get(2).shouldHave(text("@rusmail.ru"))
    }

    @Test
    fun canGetElementByIndex_fromFirstNElements_ofFilteredCollection() = runBlockingTest {
        val collection = `$$x`("//select[@name='domain']/option")
            .filterBy(text(".ru"))
            .first(2)
            .shouldHave(size(2))
        collection.get(0).shouldHave(text("@livemail.ru"))
        collection.get(1).shouldHave(text("@myrambler.ru"))
        assertThat { collection.get(2).text }
            .isFailure()
            .all {
                isInstanceOf(IndexOutOfBoundsException::class.java)
                hasMessage("Index: 2, size: 2")
            }
    }

    @Test
    fun canGetLastNElements() = runBlockingTest {
        val collection = `$$x`("//select[@name='domain']/option")
        collection.last(2).shouldHaveSize(2)
        collection.last(10).shouldHaveSize(collection.getSize())
        val regularSublist: List<String> = `$$x`("//select[@name='domain']/option").asFlow()
            .map { obj: SelenideElement -> obj.text }
            .toList()
            .subList(2, collection.getSize())
        val selenideSublist: List<String> = collection.last(2).asFlow()
            .map { obj: SelenideElement -> obj.text }
            .toList()
        Assertions.assertThat(selenideSublist).isEqualTo(regularSublist)
    }

    @Test
    fun canGetElementByIndex_fromLastNElements_ofFilteredCollection() = runBlockingTest {
        val collection = `$$x`("//select[@name='domain']/option")
            .filterBy(text(".ru"))
            .last(2)
            .shouldHave(size(2))
        collection.get(0).shouldHave(text("@myrambler.ru"))
        collection.get(1).shouldHave(text("@rusmail.ru"))
        assertThat { collection.get(2).text }
            .isFailure()
            .all {
                isInstanceOf(IndexOutOfBoundsException::class.java)
                hasMessage("Index: 3")
            }
        assertThat { collection.get(3).text }
            .isFailure()
            .all {
                isInstanceOf(IndexOutOfBoundsException::class.java)
                hasMessage("Index: 4")
            }
    }

    @Test
    fun canChainFilterAndFirst() = runBlockingTest {
        `$$`("div").filterBy(Condition.visible).first()
            .shouldBe(Condition.visible)
            .shouldHave(text("non-clickable element"))
        `$$`("div").filterBy(Condition.visible).get(2).click()
    }

    @Test
    fun shouldThrow_ElementNotFound_causedBy_IndexOutOfBoundsException_first() = runBlockingTest {
        val elementsCollection = `$$`("not-existing-locator").first().`$$`("#multirowTable")
        val description = "Check throwing ElementNotFound for %s"
        assertThat { elementsCollection.shouldHaveSize(1) }
            .`as`(description, "shouldHaveSize")
            .isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                cause().isNotNull().hasClass(NoSuchElementException::class.java)
            }
        assertThat { elementsCollection.shouldHave(size(1)) }
            .`as`(description, "size")
            .isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                cause().isNotNull().hasClass(NoSuchElementException::class.java)
            }
        assertThat { elementsCollection.shouldHave(sizeGreaterThan(0)) }
            .`as`(description, "sizeGreaterThan")
            .isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                cause().isNotNull().hasClass(NoSuchElementException::class.java)
            }
        assertThat { elementsCollection.shouldHave(sizeGreaterThanOrEqual(1)) }
            .`as`(description, "sizeGreaterThanOrEqual")
            .isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                cause().isNotNull().hasClass(NoSuchElementException::class.java)
            }
        assertThat { elementsCollection.shouldHave(sizeNotEqual(0)) }
            .`as`(description, "sizeNotEqual")
            .isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                cause().isNotNull().hasClass(NoSuchElementException::class.java)
            }
        assertThat { elementsCollection.shouldHave(sizeLessThan(0)) }
            .`as`(description, "sizeLessThan")
            .isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                cause().isNotNull().hasClass(NoSuchElementException::class.java)
            }
        assertThat { elementsCollection.shouldHave(sizeLessThanOrEqual(-1)) }
            .`as`(description, "sizeLessThanOrEqual")
            .isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                cause().isNotNull().hasClass(NoSuchElementException::class.java)
            }
        assertThat { elementsCollection.shouldHave(exactTexts("any text")) }
            .`as`(description, "exactTexts")
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().hasClass(NoSuchElementException::class.java)
            }
        assertThat { elementsCollection.shouldHave(texts("any text")) }
            .`as`(description, "texts")
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().hasClass(NoSuchElementException::class.java)
            }
        assertThat { elementsCollection.shouldHave(itemWithText("any text")) }
            .`as`(description, "itemWithText")
            .isFailure()
            .all {
                isInstanceOf(ElementWithTextNotFound::class.java)
                cause().isNotNull().hasClass(NoSuchElementException::class.java)
            }
    }

    @Test
    fun shouldThrow_ElementNotFound_causedBy_IndexOutOfBoundsException() = runBlockingTest {
        val elementsCollection = `$$`("not-existing-locator").get(1).`$$`("#multirowTable")
        val description = "Check throwing ElementNotFound for %s"
        assertThat { elementsCollection.shouldHaveSize(1) }
            .`as`(description, "shouldHaveSize")
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().hasClass(IndexOutOfBoundsException::class.java)
            }
        assertThat { elementsCollection.shouldHave(size(1)) }
            .`as`(description, "size")
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().hasClass(IndexOutOfBoundsException::class.java)
            }
        assertThat { elementsCollection.shouldHave(sizeGreaterThan(0)) }
            .`as`(description, "sizeGreaterThan")
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().hasClass(IndexOutOfBoundsException::class.java)
            }
        assertThat { elementsCollection.shouldHave(sizeGreaterThanOrEqual(1)) }
            .`as`(description, "sizeGreaterThanOrEqual")
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().hasClass(IndexOutOfBoundsException::class.java)
            }
        assertThat { elementsCollection.shouldHave(sizeNotEqual(0)) }
            .`as`(description, "sizeNotEqual")
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().hasClass(IndexOutOfBoundsException::class.java)
            }
        assertThat { elementsCollection.shouldHave(sizeLessThan(0)) }
            .`as`(description, "sizeLessThan")
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().hasClass(IndexOutOfBoundsException::class.java)
            }
        assertThat { elementsCollection.shouldHave(sizeLessThanOrEqual(-1)) }
            .`as`(description, "sizeLessThanOrEqual")
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().hasClass(IndexOutOfBoundsException::class.java)
            }
        assertThat { elementsCollection.shouldHave(exactTexts("any text")) }
            .`as`(description, "exactTexts")
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().hasClass(IndexOutOfBoundsException::class.java)
            }
        assertThat { elementsCollection.shouldHave(texts("any text")) }
            .`as`(description, "texts")
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().hasClass(IndexOutOfBoundsException::class.java)
            }
        assertThat { elementsCollection.shouldHave(itemWithText("any text")) }
            .`as`(description, "itemWithText")
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().hasClass(IndexOutOfBoundsException::class.java)
            }
    }

    @Test
    fun errorWhenFindInLastElementOfEmptyCollection() = runBlockingTest {
        assertThat { `$$`("#not_exist").last().`$`("#multirowTable").should(Condition.exist) }
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                message().isNotNull().startsWith("Element not found {#not_exist:last}")
                cause().isNotNull().isInstanceOf(IndexOutOfBoundsException::class.java)
            }
    }

    @Test
    fun errorWhenFindCollectionInLastElementOfEmptyCollection() = runBlockingTest {
        assertThat { `$$`("#not_exist").last().`$$`("#multirowTable").shouldHaveSize(1) }
            .isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                message().isNotNull().startsWith("Element not found {#not_exist:last/#multirowTable}")
                cause().isNotNull().isInstanceOf(IndexOutOfBoundsException::class.java)
            }
    }

    @Test
    fun shouldHaveZeroSizeWhenFindCollectionInLastElementOfEmptyCollection() = runBlockingTest {
        `$$`("#not_exist").last().`$$`("#multirowTable").shouldHaveSize(0)
    }

    @Test
    fun shouldHaveZeroSizeWhenFindCollectionInLastElementOfFullCollection() = runBlockingTest {
        `$$`("#user-table td").last().`$$`("#not_exist").shouldHaveSize(0)
    }

    @Test
    fun shouldAnyMatchPredicate() = runBlockingTest {
        `$$`("#radioButtons input")
            .shouldBe(anyMatch(
                "value==cat"
            ) { el -> el.getAttribute("value").equals("cat") })
    }

    @Test
    fun errorWhenAnyNotMatchedButShouldBe() = runBlockingTest {
        assertThat {
            `$$`("#radioButtons input").shouldBe(anyMatch(
                "value==dog"
            ) { el -> el.getAttribute("value").equals("dog") })
        }
            .isFailure()
            .all {
                isInstanceOf(MatcherError::class.java)
                messageContains(
                    String.format(
                        "Collection matcher error" +
                            "\nExpected: any of elements to match [value==dog] predicate"
                    )
                )
            }
    }

    @Test
    fun shouldAllMatchPredicate() = runBlockingTest {
        `$$`("#radioButtons input")
            .shouldBe(allMatch(
                "name==me"
            ) { el -> el.getAttribute("name").equals("me") })
    }

    @Test
    fun errorWhenAllNotMatchedButShouldBe() = runBlockingTest {
        assertThat {
            `$$`("#radioButtons input").shouldBe(allMatch(
                "value==cat"
            ) { el -> el.getAttribute("value").equals("cat") })
        }
            .isFailure()
            .all {
                isInstanceOf(MatcherError::class.java)
                messageContains(
                    String.format(
                        "Collection matcher error" +
                            "\nExpected: all of elements to match [value==cat] predicate"
                    )
                )
            }
    }

    @Test
    fun shouldNoneMatchPredicate() = runBlockingTest {
        `$$`("#radioButtons input")
            .shouldBe(noneMatch(
                "name==you"
            ) { el -> el.getAttribute("name").equals("you") })
    }

    @Test
    fun errorWhenSomeMatchedButNoneShould() = runBlockingTest {
        assertThat {
            `$$`("#radioButtons input").shouldBe(noneMatch(
                "value==cat"
            ) { el -> el.getAttribute("value").equals("cat") })
        }
            .isFailure()
            .all {
                isInstanceOf(MatcherError::class.java)
                messageContains(
                    String.format(
                        "Collection matcher error" +
                            "\nExpected: none of elements to match [value==cat] predicate"
                    )
                )
            }
    }

    @Test
    fun shouldItemWithText() = runBlockingTest {
        `$$`("#user-table tbody tr td.firstname")
            .shouldBe(itemWithText("Bob"))
    }

    @Test
    fun errorWhenItemWithTextNotMatchedButShouldBe() = runBlockingTest {
        val expectedText = "Luis"
        assertThat { `$$`("#user-table tbody tr td.firstname").shouldHave(itemWithText(expectedText)) }
            .isFailure()
            .all {
                isInstanceOf(ElementWithTextNotFound::class.java)
                messageContains(
                    String.format(
                        "Element with text not found" +
                            "\nActual: %s" +
                            "\nExpected: %s", Arrays.asList("Bob", "John"), listOf(expectedText)
                    )
                )
            }
    }

    @Test
    fun shouldContainTexts() = runBlockingTest {
        `$$`("#hero option")
            .should(containExactTextsCaseSensitive("Denzel Washington", "John Mc'Lain", "Arnold \"Schwarzenegger\""))
        `$$`("#user-table th")
            .should(containExactTextsCaseSensitive("First name", "Last name"))
    }

    @Test
    fun errorWhenCollectionDoesNotContainTextsButShould() = runBlockingTest {
        val expectedTexts = Arrays.asList("@livemail.ru", "@yandex.ru", "@list.ru")
        val actualTexts = Arrays.asList("@livemail.ru", "@myrambler.ru", "@rusmail.ru", "@мыло.ру")
        val difference = Arrays.asList("@yandex.ru", "@list.ru")
        assertThat {
            `$$`("[name='domain'] > option").should(
                containExactTextsCaseSensitive(
                    expectedTexts
                )
            )
        }
            .isFailure()
            .all {
                isInstanceOf(DoesNotContainTextsError::class.java)
                messageContains(
                    String.format(
                        "The collection with text elements: %s\n" +
                            "should contain all of the following text elements: %s\n" +
                            "but could not find these elements: %s\n",
                        actualTexts, expectedTexts, difference
                    )
                )
            }
    }

    @Test
    fun collectionDescribe() = runBlockingTest {
        Assertions.assertThat(`$$`("not-existing-locator").describe())
            .isEqualTo("not-existing-locator []")
        Assertions.assertThat(`$$`("input[type=checkbox].red").`as`("red checkboxes").describe())
            .isEqualTo("red checkboxes []")
        Assertions.assertThat(`$$`(".active").first(42).describe())
            .isEqualTo(".active:first(42) []")
        Assertions.assertThat(`$$`(".parent").first(2).filterBy(cssClass("child")).describe())
            .isEqualTo(".parent:first(2).filter(css class 'child') []")
    }
}

private fun <T> Assert<T>.`as`(description: String, param: String): Assert<T> {
    return transform(String.format(description, param)) { it }
}
