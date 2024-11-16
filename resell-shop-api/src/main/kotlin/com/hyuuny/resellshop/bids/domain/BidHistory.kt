package com.hyuuny.resellshop.bids.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "bid_histories")
class BidHistory(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    val bidId: Long,
    @Enumerated(EnumType.STRING) val type: BidType,
    @Enumerated(EnumType.STRING) val status: BidStatus,
    val userId: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun of(bidId: Long, type: BidType, status: BidStatus, userId: Long, createdAt: LocalDateTime): BidHistory =
            BidHistory(
                bidId = bidId,
                type = type,
                status = status,
                userId = userId,
                createdAt = createdAt,
            )
    }
}
