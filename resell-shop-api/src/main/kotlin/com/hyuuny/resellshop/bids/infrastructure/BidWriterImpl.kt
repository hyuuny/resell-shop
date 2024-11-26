package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.dataaccess.BidRepository
import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.domain.BidStatus.Companion.ongoingStatuses
import com.hyuuny.resellshop.bids.domain.BidType
import com.hyuuny.resellshop.core.common.exception.AlreadyExistBidException
import com.hyuuny.resellshop.core.common.exception.InvalidBidPriceException
import com.hyuuny.resellshop.utils.generateOrderNumber
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BidWriterImpl(
    private val repository: BidRepository,
) : BidWriter {

    override fun insert(insertBid: InsertBid): Bid {
        verifyExistsBid(insertBid.type, insertBid.userId, insertBid.productSizeId)
        validateBidPrice(insertBid.price)

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

    override fun changePrice(changePriceBid: ChangePriceBid) {
        validateBidPrice(changePriceBid.newPrice)

        val bid = changePriceBid.bid
        bid.changePrice(changePriceBid.newPrice)
    }

    private fun verifyExistsBid(type: BidType, userId: Long, productSizeId: Long) {
        repository.findByTypeAndUserIdAndProductSizeIdAndStatusIn(
            type = type,
            userId = userId,
            productSizeId = productSizeId,
            status = ongoingStatuses
        )?.let {
            throw AlreadyExistBidException("이미 해당 상품에 대한 입찰이 존재합니다.")
        }
    }

    private fun validateBidPrice(price: Long) {
        if (price <= 0) throw InvalidBidPriceException("입찰 가격은 0보다 커야 합니다.")
    }
}