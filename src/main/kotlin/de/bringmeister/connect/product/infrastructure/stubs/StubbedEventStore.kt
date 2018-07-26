package de.bringmeister.connect.product.infrastructure.stubs

import de.bringmeister.connect.product.domain.Event
import de.bringmeister.connect.product.domain.EventStore
import de.bringmeister.connect.product.domain.product.MasterDataUpdatedEvent
import de.bringmeister.connect.product.domain.product.MediaDataUpdatedEvent
import de.bringmeister.connect.product.domain.product.ProductCreatedEvent
import de.bringmeister.connect.product.domain.product.ProductNumber
import org.springframework.stereotype.Service

@Service
class StubbedEventStore : EventStore {

    private val events = mutableListOf<Event>()

    override fun save(event: Event) {
        events.add(event)
    }

    override fun allFor(productNumber: ProductNumber): List<Event> {
        return events.filter {
            when (it) {
                is ProductCreatedEvent -> it.productNumber == productNumber
                is MasterDataUpdatedEvent -> it.productNumber == productNumber
                is MediaDataUpdatedEvent -> it.productNumber == productNumber
                else -> false
            }
        }
    }

    override fun exists(productNumber: ProductNumber): Boolean {
        return allFor(productNumber).isNotEmpty()
    }

    fun clear() {
        events.clear()
    }
}