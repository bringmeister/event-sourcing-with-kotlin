package de.bringmeister.connect.product.framework

interface MessageBus {
    fun send(message: Message)

    fun sendAll(messages: List<Message>) {
        messages.forEach(this::send)
    }
}
