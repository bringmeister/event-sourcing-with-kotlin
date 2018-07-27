package de.bringmeister.connect.product.ports.rest

import de.bringmeister.connect.product.domain.EventStore
import de.bringmeister.connect.product.domain.product.Product
import de.bringmeister.connect.product.domain.product.ProductNumber
import de.bringmeister.connect.product.framework.MessageBus
import de.bringmeister.connect.product.ports.messages.MasterDataUpdateMessage
import de.bringmeister.connect.product.ports.messages.MediaDataUpdateMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * A REST controller to simulate the two starting points of the demo application:
 *
 *  - The "MasterDataUpdateMessage" is send by the "Master Data Service".
 *  - The "MediaDataUpdateMessage" is send by the "Media Data Service".
 *
 * See the "README.md" for an overview of the business process!
 */
@RestController
class DemoController(
    private val messageBus: MessageBus,
    private val eventStore: EventStore
) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping("/products/{productNumber}/masterdata")
    fun updateMasterData(@PathVariable productNumber: ProductNumber, @RequestBody payload: Map<String, String>) {

        // Simulate an incoming message from another external system.
        // In our example, this message would be send by the external
        // "Master Data Service".

        log.info("A new master data update was provided! [productNumber=$productNumber]")

        messageBus.send(
            MasterDataUpdateMessage(
                productNumber = productNumber,
                name = payload["name"]!!,
                description = payload["description"]!!
            )
        )
    }

    @PostMapping("/products/{productNumber}/mediadata")
    fun updateMediaData(@PathVariable productNumber: ProductNumber, @RequestBody payload: Map<String, String>) {

        // Simulate an incoming message from another external system.
        // In our example, this message would be send by the external
        // "Media Data Service".

        log.info("A new media data update was provided! [productNumber=$productNumber]")

        messageBus.send(
            MediaDataUpdateMessage(
                productNumber = productNumber,
                imageUrl = payload["imageUrl"]!!
            )
        )
    }

    @GetMapping("/products/{productNumber}/events")
    fun getEvents(@PathVariable productNumber: ProductNumber): List<String> {
        return eventStore
            .allFor(productNumber)
            .map { it.toString() }
    }

    @GetMapping("/products/{productNumber}")
    fun getProduct(@PathVariable productNumber: ProductNumber): Product {
        val events = eventStore.allFor(productNumber)
        return Product().applyAll(events)
    }
}
