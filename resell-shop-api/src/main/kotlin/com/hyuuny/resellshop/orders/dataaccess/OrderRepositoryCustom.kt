package com.hyuuny.resellshop.orders.infrastructure

import com.hyuuny.resellshop.core.common.response.SimplePage
import com.hyuuny.resellshop.orders.domain.Order
import com.hyuuny.resellshop.orders.service.OrderSearchCommand
import org.springframework.data.domain.Pageable

interface OrderRepositoryCustom {
    fun findAllBySearchCommand(searchCommand: OrderSearchCommand, pageable: Pageable): SimplePage<Order>
}
