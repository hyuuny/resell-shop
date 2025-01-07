package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.bids.domain.Bid

interface BidReader {

    fun read(id: Long): Bid

    fun read(ids: Collection<Long>): List<Bid>

    fun readMinPriceProductSizes(productSizeIds: Collection<Long>): List<BidPriceDetailsResponse>

}