package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.domain.BidStatus.Companion.ongoingStatuses
import com.hyuuny.resellshop.bids.infrastructure.BidRepository
import com.hyuuny.resellshop.core.common.exception.AlreadyExistBidException
import com.hyuuny.resellshop.core.common.exception.InvalidBidPriceException
import com.hyuuny.resellshop.core.logging.Log
import com.hyuuny.resellshop.utils.generateOrderNumber
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class BidService(
    private val repository: BidRepository,
) {
    companion object : Log

    @Transactional
    fun create(command: CreateBidCommand): BidResponse {
        verifyExistsBid(command)
        validateBidPrice(command.price)

        log.info("입찰 등록 요청: $command")
        val now = LocalDateTime.now()
        val bid = Bid.of(
            type = command.type,
            orderNumber = generateOrderNumber(now),
            userId = command.userId,
            productId = command.productId,
            productSizeId = command.productSizeId,
            price = command.price,
            createdAt = now,
        )
        return BidResponse(repository.save(bid))
    }

    private fun verifyExistsBid(command: CreateBidCommand) {
        repository.findByTypeAndUserIdAndProductSizeIdAndStatusIn(
            type = command.type,
            userId = command.userId,
            productSizeId = command.productSizeId,
            status = ongoingStatuses
        )?.let {
            throw AlreadyExistBidException("이미 해당 상품에 대한 입찰이 존재합니다.")
        }
    }

    private fun validateBidPrice(price: Long) {
        if (price <= 0) throw InvalidBidPriceException("입찰 가격은 0보다 커야 합니다.")
    }
}