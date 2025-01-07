package com.hyuuny.resellshop.orders.service

import com.hyuuny.resellshop.orders.domain.OrderStatus
import java.time.LocalDate
import java.time.LocalDateTime

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
    fun toNewOrder(): NewOrder = NewOrder(
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

data class OrderSearchCommand(
    val orderNumber: String? = null,
    val status: OrderStatus? = null,
    val sellerId: Long? = null,
    val buyerId: Long? = null,
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
) {
    fun getMaxToDateTime(): LocalDateTime? = toDate?.atTime(23, 59, 59)
}
