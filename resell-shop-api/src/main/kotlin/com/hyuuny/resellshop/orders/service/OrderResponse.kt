package com.hyuuny.resellshop.orders.service

import com.hyuuny.resellshop.orders.domain.Order
import com.hyuuny.resellshop.orders.domain.OrderStatus
import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val status: OrderStatus,
    val orderNumber: String,
    val sellerId: Long,
    val buyerId: Long,
    val bidId: Long,
    val commission: Long,
    val deliveryFee: Long,
    val productPrice: Long,
    val totalPrice: Long,
    val createdAt: LocalDateTime,
) {
    constructor(entity: Order) : this(
        id = entity.id!!,
        status = entity.status,
        orderNumber = entity.orderNumber,
        sellerId = entity.sellerId,
        buyerId = entity.buyerId,
        bidId = entity.bidId,
        commission = entity.commission.amount,
        deliveryFee = entity.deliveryFee.amount,
        productPrice = entity.productPrice.amount,
        totalPrice = entity.totalPrice.amount,
        createdAt = entity.createdAt,
    )
}