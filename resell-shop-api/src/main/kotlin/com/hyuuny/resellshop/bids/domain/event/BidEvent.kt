package com.hyuuny.resellshop.bids.domain.event

import com.hyuuny.resellshop.bids.domain.BidStatus

data class BidStatusChangedEvent(
    val bidId: Long,
    val status: BidStatus,
)
