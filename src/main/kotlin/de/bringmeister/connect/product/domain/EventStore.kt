package de.bringmeister.connect.product.domain

import de.bringmeister.connect.product.domain.product.ProductNumber

interface EventStore {
    fun save(event: Event)

    fun saveAll(events: List<Event>) {
        events.forEach(this::save)
    }

    fun allFor(productNumber: ProductNumber): List<Event>

    fun exists(productNumber: ProductNumber): Boolean
}
