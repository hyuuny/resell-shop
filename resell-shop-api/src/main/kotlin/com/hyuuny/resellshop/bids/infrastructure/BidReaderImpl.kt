package com.hyuuny.resellshop.bids.infrastructure

import com.hyuuny.resellshop.bids.dataaccess.BidRepository
import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.BidStatus.Companion.ongoingStatuses
import com.hyuuny.resellshop.bids.domain.BidType
import com.hyuuny.resellshop.bids.service.BidPriceDetailsResponse
import com.hyuuny.resellshop.core.common.exception.BidNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class BidReaderImpl(
    private val repository: BidRepository,
) : BidReader {

    override fun findById(id: Long): Bid =
        repository.findByIdOrNull(id) ?: throw BidNotFoundException("입찰 내역을 찾을 수 없습니다. id: $id")

    override fun findByTypeAndUserIdAndProductSizeIdAndStatusIn(
        type: BidType,
        userId: Long,
        productSizeId: Long,
        status: Collection<BidStatus>
    ): Bid? = repository.findByTypeAndUserIdAndProductSizeIdAndStatusIn(
        type = type,
        userId = userId,
        productSizeId = productSizeId,
        status = ongoingStatuses
    )

    override fun findAllMinPriceByProductSizeIdIn(productSizeIds: Collection<Long>): List<BidPriceDetailsResponse> =
        repository.findAllMinPriceByProductSizeIdIn(productSizeIds)
}