package de.bringmeister.connect.product.ports.messages

import de.bringmeister.connect.product.domain.product.ProductNumber
import de.bringmeister.connect.product.framework.Message

data class MediaDataUpdateMessage(
    val productNumber: ProductNumber,
    val imageUrl: String
) : Message
