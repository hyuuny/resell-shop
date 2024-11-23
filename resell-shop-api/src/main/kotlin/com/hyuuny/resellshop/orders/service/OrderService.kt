package com.hyuuny.resellshop.orders.service

import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.event.BidStatusChangedEvent
import com.hyuuny.resellshop.orders.infrastructure.OrderWriter
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class OrderService(
    private val writer: OrderWriter,
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Transactional
    fun create(command: CreateOrderCommand): OrderResponse {
        val newOrder = writer.insert(command.toInsertOrder())
        eventPublisher.publishEvent(BidStatusChangedEvent(newOrder.bidId, BidStatus.COMPLETED))
        return OrderResponse(newOrder)
    }

}
