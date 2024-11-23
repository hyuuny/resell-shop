package com.hyuuny.resellshop.orders.infrastructure

import com.hyuuny.resellshop.orders.dataaccess.OrderRepository
import com.hyuuny.resellshop.orders.domain.Order
import com.hyuuny.resellshop.orders.domain.OrderStatus
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OrderWriterImpl(
    private val repository: OrderRepository,
) : OrderWriter {

    override fun insert(insertOrder: InsertOrder): Order = repository.save(
        Order.of(
            status = OrderStatus.CREATED,
            orderNumber = insertOrder.orderNumber,
            sellerId = insertOrder.sellerId,
            buyerId = insertOrder.buyerId,
            bidId = insertOrder.bidId,
            commission = insertOrder.commission,
            deliveryFee = insertOrder.deliveryFee,
            productPrice = insertOrder.productPrice,
            totalPrice = insertOrder.totalPrice,
            createdAt = LocalDateTime.now(),
        )
    )
}