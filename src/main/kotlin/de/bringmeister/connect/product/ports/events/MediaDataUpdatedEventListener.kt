package de.bringmeister.connect.product.ports.events

import de.bringmeister.connect.product.application.cdn.UpdateCdnCommand
import de.bringmeister.connect.product.domain.CommandBus
import de.bringmeister.connect.product.domain.EventListener
import de.bringmeister.connect.product.domain.product.MediaDataUpdatedEvent
import org.springframework.stereotype.Component

@Component
class MediaDataUpdatedEventListener(private val commandBus: CommandBus) {

    @EventListener
    fun handle(event: MediaDataUpdatedEvent) {

        commandBus.send(
            UpdateCdnCommand(
                productNumber = event.productNumber
            )
        )
    }
}
