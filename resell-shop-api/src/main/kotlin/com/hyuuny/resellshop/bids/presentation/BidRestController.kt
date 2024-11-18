package com.hyuuny.resellshop.bids.presentation

import com.hyuuny.resellshop.bids.service.BidResponse
import com.hyuuny.resellshop.bids.service.BidService
import com.hyuuny.resellshop.bids.service.ProductBidPriceResponse
import com.hyuuny.resellshop.core.common.response.ResellShopResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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

}
