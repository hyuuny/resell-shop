package com.hyuuny.resellshop.orders.presentation

import com.hyuuny.resellshop.orders.service.CreateOrderCommand

data class CreateOrderRequest(
    val orderNumber: String,
    val sellerId: Long,
    val buyerId: Long,
    val bidId: Long,
    val commission: Long,
    val deliveryFee: Long,
    val productPrice: Long,
    val totalPrice: Long,
) {
    fun toCommand() = CreateOrderCommand(
        orderNumber = orderNumber,
        sellerId = sellerId,
        buyerId = buyerId,
        bidId = bidId,
        commission = commission,
        deliveryFee = deliveryFee,
        productPrice = productPrice,
        totalPrice = totalPrice,
    )
}
