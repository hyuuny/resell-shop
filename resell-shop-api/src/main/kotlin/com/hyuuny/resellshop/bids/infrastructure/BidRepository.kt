package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.BidType
import org.springframework.data.jpa.repository.JpaRepository

interface BidRepository : JpaRepository<Bid, Long>, BidRepositoryCustom {
    fun findByTypeAndUserIdAndProductSizeIdAndStatusIn(
        type: BidType,
        userId: Long,
        productSizeId: Long,
        status: Collection<BidStatus>,
    ): Bid?
}