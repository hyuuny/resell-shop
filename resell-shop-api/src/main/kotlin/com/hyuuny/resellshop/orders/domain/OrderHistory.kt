package com.hyuuny.resellshop.orders.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "order_histories")
class OrderHistory(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    val orderId: Long,
    @Enumerated(EnumType.STRING) val status: OrderStatus,
    val sellerId: Long,
    val buyerId: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun of(
            orderId: Long,
            status: OrderStatus,
            sellerId: Long,
            buyerId: Long,
            createdAt: LocalDateTime
        ): OrderHistory =
            OrderHistory(
                orderId = orderId,
                status = status,
                sellerId = sellerId,
                buyerId = buyerId,
                createdAt = createdAt,
            )
    }
}
