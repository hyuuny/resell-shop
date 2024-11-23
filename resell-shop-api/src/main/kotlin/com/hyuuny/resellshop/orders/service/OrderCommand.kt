package com.hyuuny.resellshop.orders.service

import com.hyuuny.resellshop.orders.infrastructure.InsertOrder

data class CreateOrderCommand(
    val orderNumber: String,
    val sellerId: Long,
    val buyerId: Long,
    val bidId: Long,
    val commission: Long,
    val deliveryFee: Long,
    val productPrice: Long,
    val totalPrice: Long,
) {
    fun toInsertOrder(): InsertOrder {
        return InsertOrder(
            orderNumber = orderNumber,
            sellerId = sellerId,
            buyerId = buyerId,
            bidId = bidId,
            commission = commission,
            deliveryFee = deliveryFee,
            productPrice = productPrice,
            totalPrice = totalPrice
        )
    }
}