package com.hyuuny.resellshop.bids.presentation

import com.hyuuny.resellshop.bids.service.BidResponse
import com.hyuuny.resellshop.bids.service.BidService
import com.hyuuny.resellshop.bids.service.ProductBidPriceResponse
import com.hyuuny.resellshop.core.common.response.ResellShopResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RequestMapping("/api/v1/bids")
@RestController
class BidRestController(
    private val service: BidService,
) {

    @PostMapping
    fun create(@RequestBody @Valid request: CreateBidRequest): ResponseEntity<ResellShopResponse<BidResponse>> {
        val bid = service.create(request.toCommand())
        return ResponseEntity.created(URI.create("/api/v1/bids/${bid.id}"))
            .body(ResellShopResponse.success(bid))
    }

    @GetMapping("/products/{productId}")
    fun getAllMinPriceByProductId(
        @PathVariable productId: Long
    ): ResponseEntity<ResellShopResponse<ProductBidPriceResponse>> {
        val productBidPriceResponse = service.findAllMinPriceByProductId(productId)
        return ResponseEntity.ok(ResellShopResponse.success(productBidPriceResponse))
    }

    @PatchMapping("/{id}/change-price")
    fun changePrice(
        @PathVariable id: Long,
        @RequestBody @Valid request: ChangePriceRequest
    ): ResponseEntity<ResellShopResponse<Unit>> {
        service.changePrice(id, request.toCommand())
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<ResellShopResponse<Unit>> {
        service.delete(id)
        return ResponseEntity.ok().build()
    }

}
