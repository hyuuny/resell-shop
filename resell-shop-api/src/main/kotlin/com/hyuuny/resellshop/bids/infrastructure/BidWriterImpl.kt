package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.dataaccess.BidRepository
import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.utils.generateOrderNumber
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BidWriterImpl(
    private val repository: BidRepository,
) : BidWriter {

    override fun insert(insertBid: InsertBid): Bid {
        val now = LocalDateTime.now()
        val bid = Bid.of(
            type = insertBid.type,
            orderNumber = generateOrderNumber(now),
            userId = insertBid.userId,
            productId = insertBid.productId,
            productSizeId = insertBid.productSizeId,
            price = insertBid.price,
            createdAt = now,
        )
        return repository.save(bid)
    }
}