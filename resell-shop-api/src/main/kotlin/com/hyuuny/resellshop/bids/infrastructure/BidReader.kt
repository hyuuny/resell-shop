package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.BidType
import com.hyuuny.resellshop.bids.service.BidPriceDetailsResponse

interface BidReader {

    fun findById(id: Long): Bid

    fun findByTypeAndUserIdAndProductSizeIdAndStatusIn(
        type: BidType,
        userId: Long,
        productSizeId: Long,
        status: Collection<BidStatus>,
    ): Bid?

    fun findAllMinPriceByProductSizeIdIn(productSizeIds: Collection<Long>): List<BidPriceDetailsResponse>

}