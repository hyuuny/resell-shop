package com.hyuuny.resellshop.bids.service

import com.hyuuny.resellshop.core.logging.Log
import com.hyuuny.resellshop.products.service.ProductReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class BidService(
    private val writer: BidWriter,
    private val reader: BidReader,
    private val productReader: ProductReader,
) {
    companion object : Log

    @Transactional
    fun create(command: CreateBidCommand): BidResponse {
        log.info("입찰 등록 요청: $command")
        val newBid = writer.write(command.toNewtBid())
        return BidResponse(newBid)
    }

    @Transactional
    fun changePrice(id: Long, command: ChangePriceCommand) {
        val bid = reader.read(id)
        bid.changePrice(command.price)
    }

    fun findAllMinPriceByProductId(productId: Long): ProductBidPriceResponse {
        val product = productReader.read(productId)
        val minBidPriceDetails = reader.readMinPriceProductSizes(product.sizes.mapNotNull { it.id }.toSet())
        return ProductBidPriceResponse(productId, minBidPriceDetails)
    }

    @Transactional
    fun delete(id: Long) {
        val bid = reader.read(id)
        writer.delete(bid.id!!)
    }

}