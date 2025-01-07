package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.bids.dataaccess.BidRepository
import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.core.common.exception.BidNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class BidReader(
    private val repository: BidRepository,
) {

    fun read(id: Long): Bid =
        repository.findByIdOrNull(id) ?: throw BidNotFoundException("입찰 내역을 찾을 수 없습니다. id: $id")

    fun read(ids: Collection<Long>): List<Bid> = repository.findAllById(ids)

    fun readMinPriceProductSizes(productSizeIds: Collection<Long>): List<BidPriceDetailsResponse> =
        repository.findAllMinPriceByProductSizeIdIn(productSizeIds)
}