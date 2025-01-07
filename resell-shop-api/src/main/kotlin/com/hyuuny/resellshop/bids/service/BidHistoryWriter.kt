package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.bids.domain.BidHistory

interface BidHistoryWriter {

    fun insert(insertBidHistory: InsertBidHistory): BidHistory

}