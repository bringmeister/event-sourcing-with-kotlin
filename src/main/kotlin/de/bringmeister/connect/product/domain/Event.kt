package de.bringmeister.connect.product.domain

import de.bringmeister.connect.product.framework.Message

interface Event : Message {
    fun getDomainEntityId(): String
}
