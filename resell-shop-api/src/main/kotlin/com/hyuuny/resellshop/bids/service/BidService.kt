package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.bids.infrastructure.BidReader
import com.hyuuny.resellshop.bids.infrastructure.BidWriter
import com.hyuuny.resellshop.core.logging.Log
import com.hyuuny.resellshop.products.infrastructure.ProductReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class BidService(
    private val writer: BidWriter,
    private val reader: BidReader,
    private val validator: BidValidator,
    private val productReader: ProductReader,
) {
    companion object : Log

    @Transactional
    fun create(command: CreateBidCommand): BidResponse {
        validator.verifyExistsBid(command.type, command.userId, command.productId)
        validator.validateBidPrice(command.price)

        log.info("입찰 등록 요청: $command")
        val newBid = writer.insert(command.toInsertBid())
        return BidResponse(newBid)
    }

    @Transactional
    fun changePrice(id: Long, command: ChangePriceCommand) {
        validator.validateBidPrice(command.price)

        val bid = reader.findById(id)
        bid.changePrice(command.price)
    }

    fun findAllMinPriceByProductId(productId: Long): ProductBidPriceResponse {
        val product = productReader.findById(productId)
        val productSizeIds = product.sizes.mapNotNull { it.id }.toSet()
        val minBidPriceDetails = reader.findAllMinPriceByProductSizeIdIn(productSizeIds)
        return ProductBidPriceResponse(productId, minBidPriceDetails)
    }

}