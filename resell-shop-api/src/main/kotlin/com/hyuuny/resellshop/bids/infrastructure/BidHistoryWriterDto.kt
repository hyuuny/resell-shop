package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.BidType
import java.time.LocalDateTime

data class InsertBidHistory(
    val bidId: Long,
    val type: BidType,
    val status: BidStatus,
    val userId: Long,
    val createdAt: LocalDateTime,
) {
    constructor(bid: Bid) : this(
        bidId = bid.id!!,
        type = bid.type,
        status = bid.status,
        userId = bid.userId,
        createdAt = bid.createdAt,
    )
}