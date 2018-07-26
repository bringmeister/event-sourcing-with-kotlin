package de.bringmeister.connect.product.ports.rest

import de.bringmeister.connect.product.domain.EventBus
import de.bringmeister.connect.product.domain.EventStore
import de.bringmeister.connect.product.domain.product.Product
import de.bringmeister.connect.product.domain.product.ProductNumber
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
 *  - The "MasterDataUpdateAvailableEvent" is thrown by the "Master Data Service".
 *  - The "MediaDataUpdateAvailableEvent" is thrown by the "Media Data Service".
 *
 * See the "README.md" for an overview of the business process!
 */
@RestController
class DemoController(
    private val eventBus: EventBus,
    private val eventStore: EventStore
) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping("/products/{productNumber}/masterdata")
    fun updateMasterDataUpdate(@PathVariable productNumber: ProductNumber, @RequestBody payload: Map<String, String>) {

        // Simulate an incoming event from another external system.
        // In our example, this event would be thrown by the external
        // "Master Data Service".

        log.info("A new master data update was provided! [productNumber=$productNumber]")

        eventBus.send(
            MasterDataUpdateAvailableEvent(
                productNumber = productNumber,
                name = payload["name"]!!,
                description = payload["description"]!!
            )
        )
    }

    @PostMapping("/products/{productNumber}/mediadata")
    fun updateMediaDataUpdate(@PathVariable productNumber: ProductNumber, @RequestBody payload: Map<String, String>) {

        // Simulate an incoming event from another external system.
        // In our example, this event would be thrown by the external
        // "Media Data Service".

        log.info("A new media data update was provided! [productNumber=$productNumber]")

        eventBus.send(
            MediaDataUpdateAvailableEvent(
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
