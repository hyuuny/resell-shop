package com.hyuuny.resellshop.orders.dataaccess

import com.hyuuny.resellshop.orders.domain.OrderHistory
import org.springframework.data.jpa.repository.JpaRepository

interface OrderHistoryRepository : JpaRepository<OrderHistory, Long> {
    fun findByOrderId(orderId: Long): OrderHistory?
}
