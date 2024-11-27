package com.hyuuny.resellshop.orders.presentation

import com.hyuuny.resellshop.core.common.response.ResellShopResponse
import com.hyuuny.resellshop.core.common.response.SimplePage
import com.hyuuny.resellshop.orders.service.OrderResponse
import com.hyuuny.resellshop.orders.service.OrderSearchCommand
import com.hyuuny.resellshop.orders.service.OrderSearchResponse
import com.hyuuny.resellshop.orders.service.OrderService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RequestMapping("/api/v1/orders")
@RestController
class OrderRestController(
    private val service: OrderService,
) {

    @PostMapping
    fun create(@RequestBody @Valid request: CreateOrderRequest): ResponseEntity<ResellShopResponse<OrderResponse>> {
        val order = service.create(request.toCommand())
        return ResponseEntity.created(URI.create("/api/v1/orders/${order.id}"))
            .body(ResellShopResponse.success(order))
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: Long): ResponseEntity<ResellShopResponse<OrderResponse>> =
        ResponseEntity.ok(ResellShopResponse.success(service.findById(id)))

    @PatchMapping("/{id}/cancel")
    fun cancelOrder(@PathVariable id: Long): ResponseEntity<ResellShopResponse<Unit>> {
        service.cancel(id)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun getAllBySearchCommand(
        searchCommand: OrderSearchCommand,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<ResellShopResponse<SimplePage<OrderSearchResponse>>> {
        val orders = service.search(searchCommand, pageable)
        return ResponseEntity.ok(ResellShopResponse.success(orders))
    }
}
