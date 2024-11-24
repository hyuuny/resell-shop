package com.hyuuny.resellshop.orders.infrastructure

import com.hyuuny.resellshop.orders.domain.Order

interface OrderReader {

    fun findById(id: Long): Order

}