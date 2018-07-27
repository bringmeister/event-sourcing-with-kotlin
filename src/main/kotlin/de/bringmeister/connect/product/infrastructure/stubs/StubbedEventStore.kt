package de.bringmeister.connect.product.infrastructure.stubs

import de.bringmeister.connect.product.domain.Event
import de.bringmeister.connect.product.domain.EventStore
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
            it.getDomainEntityId() == productNumber.stringValue()
        }
    }

    override fun exists(productNumber: ProductNumber): Boolean {
        return allFor(productNumber).isNotEmpty()
    }

    fun clear() {
        events.clear()
    }
}