package com.hyuuny.resellshop.products.presentation

import com.hyuuny.resellshop.core.common.response.ResellShopResponse
import com.hyuuny.resellshop.core.common.response.SimplePage
import com.hyuuny.resellshop.products.service.ProductResponse
import com.hyuuny.resellshop.products.service.ProductSearchCommand
import com.hyuuny.resellshop.products.service.ProductSearchResponse
import com.hyuuny.resellshop.products.service.ProductService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/products")
@RestController
class ProductRestController(
    private val service: ProductService,
) {

    @GetMapping
    fun getAllBySearchCommand(
        searchCommand: ProductSearchCommand,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<ResellShopResponse<SimplePage<ProductSearchResponse>>> {
        val products = service.search(searchCommand, pageable)
        return ResponseEntity.ok(ResellShopResponse.success(products))
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ResellShopResponse<ProductResponse>> {
        val product = service.getProduct(id)
        return ResponseEntity.ok(ResellShopResponse.success(product))
    }

}
