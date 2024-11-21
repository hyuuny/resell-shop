package com.hyuuny.resellshop.bids.dataaccess

import com.hyuuny.resellshop.bids.domain.BidHistory
import org.springframework.data.jpa.repository.JpaRepository

interface BidHistoryRepository : JpaRepository<BidHistory, Long> {
    fun findByBidId(bidId: Long): BidHistory?
}
