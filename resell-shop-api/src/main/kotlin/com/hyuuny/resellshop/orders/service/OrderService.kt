package com.hyuuny.resellshop.orders.service

import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.event.BidStatusChangedEvent
import com.hyuuny.resellshop.orders.infrastructure.OrderReader
import com.hyuuny.resellshop.orders.infrastructure.OrderWriter
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class OrderService(
    private val writer: OrderWriter,
    private val reader: OrderReader,
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Transactional
    fun create(command: CreateOrderCommand): OrderResponse {
        val newOrder = writer.write(command.toNewOrder())
        eventPublisher.publishEvent(BidStatusChangedEvent(newOrder.bidId, BidStatus.COMPLETED))
        return OrderResponse(newOrder)
    }

    fun findById(id: Long): OrderResponse = OrderResponse(reader.read(id))

    @Transactional
    fun cancel(id: Long) {
        val order = reader.read(id)
        writer.cancel(order)
        eventPublisher.publishEvent(BidStatusChangedEvent(order.bidId, BidStatus.CANCELLED))
    }

}
