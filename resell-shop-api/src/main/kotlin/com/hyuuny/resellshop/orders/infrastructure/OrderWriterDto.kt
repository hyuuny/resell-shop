package com.hyuuny.resellshop.orders.infrastructure

data class NewOrder(
    val orderNumber: String,
    val sellerId: Long,
    val buyerId: Long,
    val bidId: Long,
    val commission: Long,
    val deliveryFee: Long,
    val productPrice: Long,
    val totalPrice: Long,
)