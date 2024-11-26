package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.domain.BidType

data class NewBid(
    val type: BidType,
    val userId: Long,
    val productId: Long,
    val productSizeId: Long,
    val price: Long,
)

data class ChangePriceBid(
    val bid: Bid,
    val newPrice: Long,
)