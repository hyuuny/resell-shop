package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.service.BidPriceDetailsResponse

interface BidRepositoryCustom {
    fun findAllMinPriceByProductSizeIdIn(productSizeIds: Collection<Long>): List<BidPriceDetailsResponse>
}
