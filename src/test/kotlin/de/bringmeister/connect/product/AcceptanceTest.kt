package de.bringmeister.connect.product

import de.bringmeister.connect.product.AcceptanceTest.RecordingHandler
import de.bringmeister.connect.product.AcceptanceTest.RecordingHandlerAssert.Companion.assertThat
import de.bringmeister.connect.product.application.cdn.UpdateCdnCommand
import de.bringmeister.connect.product.application.mediadata.RegisterForMediaDataUpdatesCommand
import de.bringmeister.connect.product.application.search.UpdateSearchIndexCommand
import de.bringmeister.connect.product.application.shop.UpdateShopCommand
import de.bringmeister.connect.product.domain.product.CreateNewProductCommand
import de.bringmeister.connect.product.domain.product.MasterDataUpdatedEvent
import de.bringmeister.connect.product.domain.product.MediaDataUpdatedEvent
import de.bringmeister.connect.product.domain.product.ProductCreatedEvent
import de.bringmeister.connect.product.domain.product.ProductNumber
import de.bringmeister.connect.product.domain.product.UpdateMasterDataCommand
import de.bringmeister.connect.product.domain.product.UpdateMediaDataCommand
import de.bringmeister.connect.product.framework.Message
import de.bringmeister.connect.product.framework.MessageBus
import de.bringmeister.connect.product.framework.MessageListener
import de.bringmeister.connect.product.infrastructure.stubs.StubbedEventStore
import de.bringmeister.connect.product.ports.messages.MasterDataUpdateMessage
import de.bringmeister.connect.product.ports.messages.MediaDataUpdateMessage
import org.assertj.core.api.AbstractAssert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Service
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.SECONDS

@RunWith(SpringRunner::class)
@SpringBootTest
@Import(RecordingHandler::class)
class AcceptanceTest {

    private val productNumber = ProductNumber("P-000001")
    private val name = "Coca-Cola"
    private val description = "Tasty Coca-Cola!"
    private val url = "www.my-domain.com/my-new-image.jpg"

    @Autowired
    private lateinit var eventBus: MessageBus

    @Autowired
    private lateinit var recordingHandler: RecordingHandler

    @Autowired
    private lateinit var eventStore: StubbedEventStore

    companion object {
        var latch = CountDownLatch(1)
    }

    @Before
    fun setUp() {
        eventStore.clear()
        recordingHandler.clear()
    }

    @Test
    fun `should create new product when master data is updated for the first time`() {

        val input =
            MasterDataUpdateMessage(productNumber, name, description)

        val expectedMessages = setOf(
            input,
            CreateNewProductCommand(productNumber, name, description),
            ProductCreatedEvent(productNumber, name, description),
            RegisterForMediaDataUpdatesCommand(productNumber),
            UpdateShopCommand(productNumber),
            UpdateSearchIndexCommand(productNumber),
            MediaDataUpdateMessage(productNumber, url),
            UpdateMediaDataCommand(productNumber, url),
            MediaDataUpdatedEvent(productNumber, url),
            UpdateCdnCommand(productNumber)
        )

        eventBus.send(input)

        assertThat(recordingHandler).received(expectedMessages)
    }

    @Test
    fun `should update an existing product when master data is updated`() {

        prepareAnExistingProduct()

        val input =
            MasterDataUpdateMessage(productNumber, name, description)

        val expectedMessages = setOf(
            input,
            UpdateMasterDataCommand(productNumber, name, description),
            MasterDataUpdatedEvent(productNumber, name, description),
            UpdateShopCommand(productNumber),
            UpdateSearchIndexCommand(productNumber)
        )

        eventBus.send(input)

        assertThat(recordingHandler).received(expectedMessages)
    }

    @Test
    fun `should ignore media data updates for unknown products`() {

        val input = MediaDataUpdateMessage(productNumber, url)

        val expectedMessages = setOf(
            input,
            UpdateMediaDataCommand(productNumber, url)
        )

        eventBus.send(input)

        assertThat(recordingHandler).received(expectedMessages)
    }

    @Test
    fun `should apply media data updates for existing products`() {

        prepareAnExistingProduct()

        val input = MediaDataUpdateMessage(productNumber, url)

        val expectedMessages = setOf(
            input,
            UpdateMediaDataCommand(productNumber, url),
            MediaDataUpdatedEvent(productNumber, url),
            UpdateCdnCommand(productNumber)
        )

        eventBus.send(input)

        assertThat(recordingHandler).received(expectedMessages)
    }

    private fun prepareAnExistingProduct() {
        val event = ProductCreatedEvent(productNumber, name, description)
        eventStore.save(event)
    }

    @Service
    class RecordingHandler {

        val messages = mutableSetOf<Any>()

        @MessageListener
        fun handle(message: Message) {
            messages.add(message)
            latch.countDown()
        }

        fun clear() {
            messages.clear()
        }
    }

    class RecordingHandlerAssert(recordingHandler: RecordingHandler) :
        AbstractAssert<RecordingHandlerAssert, RecordingHandler>(recordingHandler, RecordingHandlerAssert::class.java) {

        companion object {
            fun assertThat(actual: RecordingHandler): RecordingHandlerAssert {
                return RecordingHandlerAssert(actual)
            }
        }

        fun received(expectedEvents: Set<Any>): RecordingHandlerAssert {

            latch = CountDownLatch(expectedEvents.size)
            latch.await(10, SECONDS)

            if (!actual.messages.containsAll(expectedEvents)) {
                failWithMessage("Expected messages to be <%s> but was <%s>", expectedEvents, actual.messages)
            }

            return this
        }
    }
}
