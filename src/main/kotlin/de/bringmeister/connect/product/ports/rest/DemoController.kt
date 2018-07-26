package de.bringmeister.connect.product.ports.rest

import de.bringmeister.connect.product.domain.Event
import de.bringmeister.connect.product.domain.EventBus
import de.bringmeister.connect.product.domain.EventStore
import de.bringmeister.connect.product.domain.product.ProductNumber
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
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

    @PostMapping("/master_data_update")
    fun masterDataUpdate() {

        // Simulate an incoming event from another external system.
        // In our example, this event would be thrown by the external
        // "Master Data Service".

        eventBus.send(
            MasterDataUpdateAvailableEvent(
                productNumber = ProductNumber("P-000001"),
                name = "Coca Cola",
                description = "A bottle of tasty Coca Cola"
            )
        )
    }

    @PostMapping("/media_data_update")
    fun mediaDataUpdate() {

        // Simulate an incoming event from another external system.
        // In our example, this event would be thrown by the external
        // "Media Data Service".

        eventBus.send(
            MediaDataUpdateAvailableEvent(
                productNumber = ProductNumber("P-000001"),
                imageUrl = "www.my-domain.com/my-image.jpg"
            )
        )
    }

    @GetMapping("/event_store")
    fun showEventStore(): List<Event> {
        return eventStore.allFor(ProductNumber("P-000001"))
    }
}
