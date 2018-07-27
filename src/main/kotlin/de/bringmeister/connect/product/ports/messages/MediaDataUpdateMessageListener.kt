package de.bringmeister.connect.product.ports.messages

import de.bringmeister.connect.product.domain.CommandBus
import de.bringmeister.connect.product.domain.product.UpdateMediaDataCommand
import de.bringmeister.connect.product.framework.MessageListener
import org.springframework.stereotype.Component

@Component
class MediaDataUpdateMessageListener(private val commandBus: CommandBus) {

    @MessageListener
    fun handle(event: MediaDataUpdateMessage) {

        commandBus.send(
            UpdateMediaDataCommand(
                productNumber = event.productNumber,
                imageUrl = event.imageUrl
            )
        )
    }
}

