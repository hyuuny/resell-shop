package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.bids.domain.BidStatus.Companion.ongoingStatuses
import com.hyuuny.resellshop.bids.domain.BidType
import com.hyuuny.resellshop.bids.infrastructure.BidReader
import com.hyuuny.resellshop.core.common.exception.AlreadyExistBidException
import com.hyuuny.resellshop.core.common.exception.InvalidBidPriceException
import org.springframework.stereotype.Component

@Component
class BidValidator(
    private val reader: BidReader,
) {

    fun verifyExistsBid(type: BidType, userId: Long, productSizeId: Long) {
        reader.findByTypeAndUserIdAndProductSizeIdAndStatusIn(
            type = type,
            userId = userId,
            productSizeId = productSizeId,
            status = ongoingStatuses
        )?.let {
            throw AlreadyExistBidException("이미 해당 상품에 대한 입찰이 존재합니다.")
        }
    }

    fun validateBidPrice(price: Long) {
        if (price <= 0) throw InvalidBidPriceException("입찰 가격은 0보다 커야 합니다.")
    }
}