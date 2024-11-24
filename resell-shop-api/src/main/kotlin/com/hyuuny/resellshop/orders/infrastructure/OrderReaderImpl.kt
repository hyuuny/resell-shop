package com.hyuuny.resellshop.orders.infrastructure

import com.hyuuny.resellshop.core.common.exception.OrderNotFoundException
import com.hyuuny.resellshop.orders.dataaccess.OrderRepository
import com.hyuuny.resellshop.orders.domain.Order
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class OrderReaderImpl(
    private val repository: OrderRepository,
) : OrderReader {

    override fun findById(id: Long): Order = repository.findByIdOrNull(id)
        ?: throw OrderNotFoundException("주문을 찾을 수 없습니다. id: $id")
}