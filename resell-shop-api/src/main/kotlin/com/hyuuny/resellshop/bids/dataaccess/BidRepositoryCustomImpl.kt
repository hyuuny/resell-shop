package com.hyuuny.resellshop.bids.dataaccess

import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.domain.BidType
import com.hyuuny.resellshop.bids.domain.QBid.bid
import com.hyuuny.resellshop.bids.service.BidPriceDetailsResponse
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class BidRepositoryCustomImpl : QuerydslRepositorySupport(Bid::class.java), BidRepositoryCustom {

    override fun findAllMinPriceByProductSizeIdIn(productSizeIds: Collection<Long>): List<BidPriceDetailsResponse> {
        val queryResults = from(bid)
            .select(bid.productSizeId, bid.type, bid.price.amount.min())
            .where(bid.productSizeId.`in`(productSizeIds))
            .groupBy(bid.productSizeId, bid.type)
            .fetch()
            .groupBy { it.get(0, Long::class.java) }

        return productSizeIds.flatMap { productSizeId ->
            val priceMap = queryResults[productSizeId]?.associate {
                it.get(1, BidType::class.java) to (it.get(2, Long::class.java) ?: 0L)
            } ?: emptyMap()

            listOf(
                BidPriceDetailsResponse(
                    productSizeId = productSizeId,
                    type = BidType.SELL,
                    minPrice = priceMap[BidType.SELL] ?: 0
                ),
                BidPriceDetailsResponse(
                    productSizeId = productSizeId,
                    type = BidType.BUY,
                    minPrice = priceMap[BidType.BUY] ?: 0
                )
            )
        }
    }
}
