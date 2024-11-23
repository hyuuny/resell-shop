package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.dataaccess.BidHistoryRepository
import com.hyuuny.resellshop.bids.domain.BidHistory
import org.springframework.stereotype.Component

@Component
class BidHistoryWriterImpl(
    private val repository: BidHistoryRepository,
) : BidHistoryWriter {

    override fun insert(insertBidHistory: InsertBidHistory): BidHistory {
        return repository.save(
            BidHistory.of(
                bidId = insertBidHistory.bidId,
                type = insertBidHistory.type,
                status = insertBidHistory.status,
                userId = insertBidHistory.userId,
                createdAt = insertBidHistory.createdAt,
            )
        )
    }

}