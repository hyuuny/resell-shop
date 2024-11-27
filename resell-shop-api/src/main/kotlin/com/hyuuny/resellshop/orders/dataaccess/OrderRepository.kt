package com.hyuuny.resellshop.orders.dataaccess

import com.hyuuny.resellshop.orders.domain.Order
import com.hyuuny.resellshop.orders.infrastructure.OrderRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long>, OrderRepositoryCustom {
}
