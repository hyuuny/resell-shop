package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.domain.Bid

interface BidWriter {

    fun insert(insertBid: InsertBid): Bid

}