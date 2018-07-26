package de.bringmeister.connect.product.ports.messages

import de.bringmeister.connect.product.domain.CommandBus
import de.bringmeister.connect.product.domain.EventListener
import de.bringmeister.connect.product.domain.EventStore
import de.bringmeister.connect.product.domain.product.CreateNewProductCommand
import de.bringmeister.connect.product.domain.product.UpdateMasterDataCommand
import de.bringmeister.connect.product.ports.rest.MasterDataUpdateAvailableEvent
import org.springframework.stereotype.Component

@Component
class MasterDataUpdateAvailableEventListener(
    private val commandBus: CommandBus,
    private val eventStore: EventStore
) {

    @EventListener
    fun handle(event: MasterDataUpdateAvailableEvent) {

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
