package de.bringmeister.connect.product.domain.product

import de.bringmeister.connect.product.domain.DomainEntity
import de.bringmeister.connect.product.domain.Event
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.Assert.hasText

/**
 * The product domain entity.
 *
 * This entity encapsulates all information which belongs to a product.
 * It also provides business methods to work on this information. Data
 * cannot be changed from outside - there are no setters.
 *
 * Whenever data has been changed a domain event will thrown. This event
 * informs any listener that something has changed in the context of a
 * product. In a real life example, those events would be published over
 * a message broker such as Kafka, ActiveMQ or AWS Kinesis.
 */
class Product : DomainEntity<ProductNumber>() {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    var productInformation: ProductInformation? = null
        private set

    var imageUrl: String? = null // we  have none until we get the first media data update
        private set

    fun applyAll(vararg events: Event): Product {
        return applyAll(events.toList())
    }

    fun applyAll(events: List<Event>): Product {

        events.forEach {
            when (it) {
                is ProductCreatedEvent -> apply(it)
                is MasterDataUpdatedEvent -> apply(it)
                is MediaDataUpdatedEvent -> apply(it)
            }
        }

        return this
    }

    fun handle(command: CreateNewProductCommand) {

        hasText(command.name, "Product name must not be empty!")
        hasText(command.description, "Product description must not be empty!")

        raise(
            ProductCreatedEvent(
                productNumber = command.productNumber,
                name = command.name,
                description = command.description
            )
        )
        log.info("New product created. [productNumber={}]", id)
    }

    fun apply(event: ProductCreatedEvent) {
        id = event.productNumber
        productInformation = ProductInformation(
            name = event.name,
            description = event.description
        )
    }

    fun handle(command: UpdateMasterDataCommand) {

        hasText(command.name, "Product name must not be empty!")
        hasText(command.description, "Product description must not be empty!")

        raise(
            MasterDataUpdatedEvent(
                productNumber = id!!,
                name = command.name,
                description = command.description
            )
        )
        log.info("Product master data updated. [productNumber={}]", id)
    }

    fun apply(event: MasterDataUpdatedEvent) {
        this.productInformation = ProductInformation(
            name = event.name,
            description = event.description
        )
    }

    fun handle(command: UpdateMediaDataCommand) {

        hasText(command.imageUrl, "Image URL must not be empty!")

        raise(
            MediaDataUpdatedEvent(
                productNumber = id!!,
                imageUrl = command.imageUrl
            )
        )
        log.info("Product media data updated. [productNumber={}]", id)
    }

    fun apply(event: MediaDataUpdatedEvent) {
        this.imageUrl = event.imageUrl
    }
}
