package com.hyuuny.resellshop.orders.service

import com.hyuuny.resellshop.core.common.response.SimplePage
import com.hyuuny.resellshop.orders.domain.Order
import org.springframework.data.domain.Pageable

interface OrderReader {

    fun read(id: Long): Order

    fun readPage(searchCommand: OrderSearchCommand, pageable: Pageable): SimplePage<Order>

}