package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.bids.domain.BidStatus
import com.hyuuny.resellshop.bids.domain.BidType
import com.hyuuny.resellshop.bids.infrastructure.BidHistoryRepository
import com.hyuuny.resellshop.bids.infrastructure.BidRepository
import com.hyuuny.resellshop.products.TestEnvironment
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

@TestEnvironment
class BidServiceTest(
    private val repository: BidRepository,
    private val bidHistoryRepository: BidHistoryRepository,
    private val service: BidService,
) {

    @AfterEach
    fun tearDown() {
        repository.deleteAll()
        bidHistoryRepository.deleteAll()
    }

    @Test
    fun `판매자는 상품 판매 입찰을 등록할 수 있고, 입찰 히스토리도 저장된다`() {
        val command = CreateBidCommand(
            type = BidType.SELL,
            userId = 1L,
            productId = 1L,
            productSizeId = 1L,
            price = 55000,
        )

        val savedBid = service.create(command)
        val savedBidHistory = bidHistoryRepository.findByBidId(savedBid.id)

        assertThat(savedBid.id).isNotNull()
        assertThat(savedBid.type).isEqualTo(command.type)
        assertThat(savedBid.status).isEqualTo(BidStatus.WAITING)
        assertThat(savedBid.orderNumber).isNotNull()
        assertThat(savedBid.userId).isEqualTo(command.userId)
        assertThat(savedBid.productId).isEqualTo(command.productId)
        assertThat(savedBid.productSizeId).isEqualTo(command.productSizeId)
        assertThat(savedBid.price).isEqualTo(command.price)
        assertThat(savedBid.createdAt).isNotNull()

        assertThat(savedBidHistory).isNotNull()
        assertThat(savedBidHistory!!.bidId).isEqualTo(savedBid.id)
        assertThat(savedBidHistory.type).isEqualTo(savedBid.type)
        assertThat(savedBidHistory.status).isEqualTo(savedBid.status)
        assertThat(savedBidHistory.userId).isEqualTo(savedBid.userId)
    }
}