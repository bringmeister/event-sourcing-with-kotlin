package de.bringmeister.connect.product.ports.messages

import de.bringmeister.connect.product.domain.CommandBus
import de.bringmeister.connect.product.domain.EventStore
import de.bringmeister.connect.product.domain.product.CreateNewProductCommand
import de.bringmeister.connect.product.domain.product.UpdateMasterDataCommand
import de.bringmeister.connect.product.framework.MessageListener
import org.springframework.stereotype.Component

@Component
class MasterDataUpdateMessageListener(
    private val commandBus: CommandBus,
    private val eventStore: EventStore
) {

    @MessageListener
    fun handle(event: MasterDataUpdateMessage) {

        val productExists = eventStore.exists(event.productNumber)

        if (productExists) {

            commandBus.send(
                UpdateMasterDataCommand(
                    productNumber = event.productNumber,
                    name = event.name,
                    description = event.description
                )
            )
        } else {

            commandBus.send(
                CreateNewProductCommand(
                    productNumber = event.productNumber,
                    name = event.name,
                    description = event.description
                )
            )
        }
    }
}
