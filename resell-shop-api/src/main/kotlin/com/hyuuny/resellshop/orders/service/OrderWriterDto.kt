package com.hyuuny.resellshop.orders.service

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