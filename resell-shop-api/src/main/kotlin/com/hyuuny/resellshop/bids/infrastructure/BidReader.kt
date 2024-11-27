package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.service.BidPriceDetailsResponse

interface BidReader {

    fun read(id: Long): Bid

    fun read(ids: Collection<Long>): List<Bid>

    fun readMinPriceProductSizes(productSizeIds: Collection<Long>): List<BidPriceDetailsResponse>

}