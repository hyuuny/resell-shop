package com.hyuuny.resellshop.orders.domain

import com.hyuuny.resellshop.products.domain.Price
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    status: OrderStatus,
    val orderNumber: String,
    val sellerId: Long,
    val buyerId: Long,
    val bidId: Long,
    @AttributeOverride(name = "amount", column = Column(name = "commission")) val commission: Price,
    @AttributeOverride(name = "amount", column = Column(name = "delivery_fee")) val deliveryFee: Price,
    @AttributeOverride(name = "amount", column = Column(name = "product_price")) val productPrice: Price,
    @AttributeOverride(name = "amount", column = Column(name = "total_price")) val totalPrice: Price,
    val createdAt: LocalDateTime,
) {

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = status
        protected set

    companion object {
        fun of(
            status: OrderStatus,
            orderNumber: String,
            sellerId: Long,
            buyerId: Long,
            bidId: Long,
            commission: Long,
            deliveryFee: Long,
            productPrice: Long,
            totalPrice: Long,
            createdAt: LocalDateTime,
        ) = Order(
            status = status,
            orderNumber = orderNumber,
            sellerId = sellerId,
            buyerId = buyerId,
            bidId = bidId,
            commission = Price(commission),
            deliveryFee = Price(deliveryFee),
            productPrice = Price(productPrice),
            totalPrice = Price(totalPrice),
            createdAt = createdAt,
        )
    }

    fun isCancelable(): Boolean = OrderStatus.cancelableStatus.contains(status)

    fun cancel() {
        status = OrderStatus.CANCELLED
    }
}
