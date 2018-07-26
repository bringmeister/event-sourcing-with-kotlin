package de.bringmeister.connect.product.application.product

import de.bringmeister.connect.product.domain.CommandListener
import de.bringmeister.connect.product.domain.EventBus
import de.bringmeister.connect.product.domain.EventStore
import de.bringmeister.connect.product.domain.product.CreateNewProductCommand
import de.bringmeister.connect.product.domain.product.Product
import de.bringmeister.connect.product.domain.product.UpdateMasterDataCommand
import de.bringmeister.connect.product.domain.product.UpdateMediaDataCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val eventBus: EventBus,
    private val eventStore: EventStore
) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @CommandListener
    fun handle(command: CreateNewProductCommand) {
        val product = Product().applyAll(eventStore.allFor(command.productNumber))
        product.handle(command)
        val events = product.occurredEvents()
        eventBus.sendAll(events)
        eventStore.saveAll(events)
    }

    @CommandListener
    fun handle(command: UpdateMasterDataCommand) {
        val product = Product().applyAll(eventStore.allFor(command.productNumber))
        product.handle(command)
        val events = product.occurredEvents()
        eventBus.sendAll(events)
        eventStore.saveAll(events)
    }

    @CommandListener
    fun handle(command: UpdateMediaDataCommand) {
        if (eventStore.exists(command.productNumber)) {
            val product = Product().applyAll(eventStore.allFor(command.productNumber))
            product.handle(command)
            val events = product.occurredEvents()
            eventBus.sendAll(events)
            eventStore.saveAll(events)
        } else {
            log.info("Media data ignored as product doesn't exist. [productNumber={}]", command.productNumber)
        }
    }
}