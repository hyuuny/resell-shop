package com.hyuuny.resellshop.orders.service

import com.hyuuny.resellshop.core.common.exception.NotCancelableOrderException
import com.hyuuny.resellshop.orders.dataaccess.OrderRepository
import com.hyuuny.resellshop.orders.domain.Order
import com.hyuuny.resellshop.orders.domain.OrderStatus
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OrderWriterImpl(
    private val repository: OrderRepository,
) : OrderWriter {

    override fun write(newOrder: NewOrder): Order = repository.save(
        Order.of(
            status = OrderStatus.CREATED,
            orderNumber = newOrder.orderNumber,
            sellerId = newOrder.sellerId,
            buyerId = newOrder.buyerId,
            bidId = newOrder.bidId,
            commission = newOrder.commission,
            deliveryFee = newOrder.deliveryFee,
            productPrice = newOrder.productPrice,
            totalPrice = newOrder.totalPrice,
            createdAt = LocalDateTime.now(),
        )
    )

    override fun cancel(order: Order) {
        if (!order.isCancelable()) throw NotCancelableOrderException("주문을 취소할 수 상태입니다.")
        order.cancel()
    }
}