package com.hyuuny.resellshop.bids.presentation

import com.hyuuny.resellshop.bids.domain.BidType
import com.hyuuny.resellshop.bids.service.ChangePriceCommand
import com.hyuuny.resellshop.bids.service.CreateBidCommand

data class CreateBidRequest(
    val type: BidType,
    val userId: Long,
    val productId: Long,
    val productSizeId: Long,
    val price: Long,
) {
    fun toCommand() = CreateBidCommand(
        type = type,
        userId = userId,
        productId = productId,
        productSizeId = productSizeId,
        price = price,
    )
}
