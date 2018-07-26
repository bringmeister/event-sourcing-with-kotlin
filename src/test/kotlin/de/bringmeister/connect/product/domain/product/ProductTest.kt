package de.bringmeister.connect.product.domain.product

import de.bringmeister.connect.product.domain.Event
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class ProductTest {

    @Test
    fun `should create an new and empty product`() {

        val product = Product()

        assertThat(product.id).isNull()
        assertThat(product.productInformation).isNull()
        assertThat(product.productInformation).isNull()
        assertThat(product.imageUrl).isNull()
    }

    @Test
    fun `should apply ProductCreatedEvent`() {

        val product = Product().applyAll(aProductCreatedEvent())

        assertThat(product.id?.stringValue()).isEqualTo("P-000001")
        assertThat(product.productInformation?.name).isEqualTo("Coca Cola")
        assertThat(product.productInformation?.description).isEqualTo("This is a bottle of tasty Coca Cola!")
        assertThat(product.imageUrl).isNull()
    }

    @Test
    fun `should apply MasterDataUpdatedEvent`() {

        val product = Product().applyAll(aProductCreatedEvent(), aMasterDataUpdatedEvent())

        assertThat(product.id?.stringValue()).isEqualTo("P-000001")
        assertThat(product.productInformation?.name).isEqualTo("Coca Cola New Edition")
        assertThat(product.productInformation?.description).isEqualTo("This is a bottle of tasty Coca Cola in a new flavour!")
        assertThat(product.imageUrl).isNull()
    }

    @Test
    fun `should apply MediaDataUpdatedEvent`() {

        val product = Product().applyAll(aProductCreatedEvent(), aMediaDataUpdatedEvent())

        assertThat(product.id?.stringValue()).isEqualTo("P-000001")
        assertThat(product.productInformation?.name).isEqualTo("Coca Cola")
        assertThat(product.productInformation?.description).isEqualTo("This is a bottle of tasty Coca Cola!")
        assertThat(product.imageUrl).isEqualTo("www.my-domain.com/my-new-image.jpg")
    }

    @Test
    fun `should throw event when product is created`() {

        val product = Product()
        product.handle(aCreateNewProductCommand())

        val events = product.occurredEvents()
        assertThat(events).hasSize(1)
        assertThat(events[0]).isEqualTo(aProductCreatedEvent())
    }

    @Test
    fun `should throw event when master data is updated`() {

        val product = Product()
        product.applyAll(aProductCreatedEvent()) // already created!
        product.handle(anUpdateMasterDataCommand())

        val events = product.occurredEvents()
        assertThat(events).hasSize(1)
        assertThat(events[0]).isEqualTo(aMasterDataUpdatedEvent())
    }

    @Test
    fun `should throw event when media data is updated`() {

        val product = Product()
        product.applyAll(aProductCreatedEvent()) // already created!
        product.handle(anUpdateMediaDataCommand())

        val events = product.occurredEvents()
        assertThat(events).hasSize(1)
        assertThat(events[0]).isEqualTo(aMediaDataUpdatedEvent())
    }

    @Test
    fun `should clear events after returning them`() {

        val product = Product()
        product.handle(aCreateNewProductCommand())

        assertThat(product.occurredEvents()).hasSize(1)
        assertThat(product.occurredEvents()).hasSize(0) // list is empty now!
    }

    @Test
    fun `should throw exception on empty name`() {
        val product = Product()
        assertThatThrownBy {
            product.handle(
                CreateNewProductCommand(
                    productNumber = ProductNumber("P-000001"),
                    name = "", // empty!
                    description = "This is a bottle of tasty Coca Cola!"
                )
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasNoCause()
            .hasMessageContaining("Product name must not be empty!")
    }

    @Test
    fun `should throw exception on empty description`() {
        val product = Product()
        assertThatThrownBy {
            product.handle(
                CreateNewProductCommand(
                    productNumber = ProductNumber("P-000001"),
                    name = "Coca Cola",
                    description = "" // empty!
                )
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasNoCause()
            .hasMessageContaining("Product description must not be empty!")
    }

    @Test
    fun `should equal product with same ID`() {

        // We've got two products with different values but with
        // the same ID - so they represent the same product!

        val product1 = Product().applyAll(aProductCreatedEvent())
        val product2 = Product().applyAll(aProductCreatedEvent())

        assertThat(product1).isEqualTo(product2)
    }

    private fun aProductCreatedEvent(): Event {
        return ProductCreatedEvent(
            productNumber = ProductNumber("P-000001"),
            name = "Coca Cola",
            description = "This is a bottle of tasty Coca Cola!"
        )
    }

    private fun aMasterDataUpdatedEvent(): Event {
        return MasterDataUpdatedEvent(
            productNumber = ProductNumber("P-000001"),
            name = "Coca Cola New Edition",
            description = "This is a bottle of tasty Coca Cola in a new flavour!"
        )
    }

    private fun aMediaDataUpdatedEvent(): Event {
        return MediaDataUpdatedEvent(
            productNumber = ProductNumber("P-000001"),
            imageUrl = "www.my-domain.com/my-new-image.jpg"
        )
    }

    private fun aCreateNewProductCommand(): CreateNewProductCommand {
        return CreateNewProductCommand(
            productNumber = ProductNumber("P-000001"),
            name = "Coca Cola",
            description = "This is a bottle of tasty Coca Cola!"
        )
    }

    private fun anUpdateMasterDataCommand(): UpdateMasterDataCommand {
        return UpdateMasterDataCommand(
            productNumber = ProductNumber("P-000001"),
            name = "Coca Cola New Edition",
            description = "This is a bottle of tasty Coca Cola in a new flavour!"
        )
    }

    private fun anUpdateMediaDataCommand(): UpdateMediaDataCommand {
        return UpdateMediaDataCommand(
            productNumber = ProductNumber("P-000001"),
            imageUrl = "www.my-domain.com/my-new-image.jpg"
        )
    }
}
