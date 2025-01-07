package com.hyuuny.resellshop.orders.service

import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.event.BidStatusChangedEvent
import com.hyuuny.resellshop.bids.service.BidReader
import com.hyuuny.resellshop.core.common.exception.BidNotFoundException
import com.hyuuny.resellshop.core.common.response.SimplePage
import com.hyuuny.resellshop.products.service.ProductReader
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class OrderService(
    private val writer: OrderWriter,
    private val reader: OrderReader,
    private val bidReader: BidReader,
    private val productReader: ProductReader,
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

    fun search(searchCommand: OrderSearchCommand, pageable: Pageable): SimplePage<OrderSearchResponse> {
        val page = reader.readPage(searchCommand, pageable)
        if (page.content.isEmpty()) return SimplePage(emptyList(), page)

        val bidMap = bidReader.read(page.content.map { it.bidId }).associateBy { it.id }
        val productMap = productReader.read(bidMap.values.map { it.productId }).associateBy { it.id }
        return SimplePage(page.content.map {
            val bid = bidMap[it.bidId] ?: throw BidNotFoundException("입찰 내역을 찾을 수 없습니다. id: ${it.bidId}")
            val product = productMap[bid.productId] ?: throw BidNotFoundException("상품을 찾을 수 없습니다. id: ${bid.productId}")
            OrderSearchResponse(it, product)
        }, page)
    }

}
