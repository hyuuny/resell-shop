package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.bids.domain.BidType

data class CreateBidCommand(
    val type: BidType,
    val userId: Long,
    val productId: Long,
    val productSizeId: Long,
    val price: Long,
)

