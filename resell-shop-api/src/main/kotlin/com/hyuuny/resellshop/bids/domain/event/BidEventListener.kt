package com.hyuuny.resellshop.bids.domain.event

import com.hyuuny.resellshop.bids.service.BidHistoryWriter
import com.hyuuny.resellshop.bids.service.BidReader
import com.hyuuny.resellshop.bids.service.InsertBidHistory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class BidEventListener(
    private val reader: BidReader,
    private val bidHistoryWriter: BidHistoryWriter,
) {

    @Async
    @TransactionalEventListener
    fun changeStatusEvent(event: BidStatusChangedEvent) {
        val bid = reader.read(event.bidId)
        bid.changeStatus(event.status)
        bidHistoryWriter.insert(InsertBidHistory(bid))
    }
}
