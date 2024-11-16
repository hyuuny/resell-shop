package com.hyuuny.resellshop.bids.domain

import com.hyuuny.resellshop.products.domain.Price
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "bids")
class Bid(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @Enumerated(EnumType.STRING) val type: BidType,
    status: BidStatus,
    val orderNumber: String,
    val userId: Long,
    val productId: Long,
    val productSizeId: Long,
    price: Price,
    val createdAt: LocalDateTime,
) {

    @Enumerated(EnumType.STRING)
    var status: BidStatus = status
        protected set

    var price: Price = price
        protected set

    companion object {
        fun of(
            type: BidType,
            status: BidStatus = BidStatus.WAITING,
            orderNumber: String,
            userId: Long,
            productId: Long,
            productSizeId: Long,
            price: Long,
            createdAt: LocalDateTime,
        ): Bid = Bid(
            type = type,
            status = status,
            orderNumber = orderNumber,
            userId = userId,
            productId = productId,
            productSizeId = productSizeId,
            price = Price(price),
            createdAt = createdAt,
        )
    }

    fun changePrice(newPrice: Long) {
        price = Price(newPrice)
    }

    fun changeStatus(newStatus: BidStatus) {
        status = newStatus
    }
}
