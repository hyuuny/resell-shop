package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.bids.domain.BidType
import com.hyuuny.resellshop.bids.infrastructure.InsertBid

data class CreateBidCommand(
    val type: BidType,
    val userId: Long,
    val productId: Long,
    val productSizeId: Long,
    val price: Long,
) {
    fun toInsertBid(): InsertBid {
        return InsertBid(
            type = type,
            userId = userId,
            productId = productId,
            productSizeId = productSizeId,
            price = price,
        )
    }
}

data class ChangePriceCommand(
    val price: Long,
)
