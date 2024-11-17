package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.BidType
import java.time.LocalDateTime

data class BidResponse(
    val id: Long,
    val type: BidType,
    val status: BidStatus,
    val orderNumber: String,
    val userId: Long,
    val productId: Long,
    val productSizeId: Long,
    val price: Long,
    val createdAt: LocalDateTime,
) {
    constructor(entity: Bid) : this(
        id = entity.id!!,
        type = entity.type,
        status = entity.status,
        orderNumber = entity.orderNumber,
        userId = entity.userId,
        productId = entity.productId,
        productSizeId = entity.productSizeId,
        price = entity.price.amount,
        createdAt = entity.createdAt,
    )
}

data class ProductBidPriceResponse(
    val productId: Long,
    val bidPriceDetails: List<BidPriceDetailsResponse>,
)

data class BidPriceDetailsResponse(
    val productSizeId: Long?,
    val type: BidType?,
    val minPrice: Long?,
)
