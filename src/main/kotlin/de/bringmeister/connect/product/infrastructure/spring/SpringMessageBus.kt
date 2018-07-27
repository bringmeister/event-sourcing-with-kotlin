package de.bringmeister.connect.product.infrastructure.spring

import de.bringmeister.connect.product.framework.Message
import de.bringmeister.connect.product.framework.MessageBus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SpringMessageBus(private val publisher: ApplicationEventPublisher) : MessageBus {

    override fun send(message: Message) {
        publisher.publishEvent(message)
    }
}
