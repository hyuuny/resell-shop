package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.bids.domain.Bid
import com.hyuuny.resellshop.bids.domain.BidStatus.Companion.ongoingStatuses
import com.hyuuny.resellshop.bids.infrastructure.BidRepository
import com.hyuuny.resellshop.core.common.exception.AlreadyExistBidException
import com.hyuuny.resellshop.core.common.exception.BidNotFoundException
import com.hyuuny.resellshop.core.common.exception.InvalidBidPriceException
import com.hyuuny.resellshop.core.common.exception.ProductNotFoundException
import com.hyuuny.resellshop.core.logging.Log
import com.hyuuny.resellshop.products.infrastructure.ProductRepository
import com.hyuuny.resellshop.utils.generateOrderNumber
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class BidService(
    private val repository: BidRepository,
    private val productRepository: ProductRepository,
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

    @Transactional
    fun changePrice(id: Long, command: ChangePriceCommand) {
        validateBidPrice(command.price)

        val bid = repository.findByIdOrNull(id) ?: throw BidNotFoundException("입찰 내역을 찾을 수 없습니다. id: $id")
        bid.changePrice(command.price)
    }

    fun findAllMinPriceByProductId(productId: Long): ProductBidPriceResponse {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw ProductNotFoundException("상품을 찾을 수 없습니다. id: $productId")
        val productSizeIds = product.sizes.mapNotNull { it.id }.toSet()
        val minBidPriceDetails = repository.findAllMinPriceByProductSizeIdIn(productSizeIds)
        return ProductBidPriceResponse(productId, minBidPriceDetails)
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