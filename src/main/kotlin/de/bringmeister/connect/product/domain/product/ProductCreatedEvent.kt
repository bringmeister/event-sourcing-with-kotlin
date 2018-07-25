package de.bringmeister.connect.product.domain.product

import de.bringmeister.connect.product.domain.Event

data class ProductCreatedEvent(
    val productNumber: ProductNumber
) : Event
