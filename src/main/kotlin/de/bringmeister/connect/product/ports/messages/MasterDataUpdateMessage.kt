package de.bringmeister.connect.product.ports.messages

import de.bringmeister.connect.product.domain.product.ProductNumber
import de.bringmeister.connect.product.framework.Message

data class MasterDataUpdateMessage(
    val productNumber: ProductNumber,
    val name: String,
    val description: String
) : Message
