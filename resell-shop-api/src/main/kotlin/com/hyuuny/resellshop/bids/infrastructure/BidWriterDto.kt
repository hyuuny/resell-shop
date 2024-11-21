package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.domain.BidType

data class InsertBid(
    val type: BidType,
    val userId: Long,
    val productId: Long,
    val productSizeId: Long,
    val price: Long,
)