package com.hyuuny.resellshop.orders.service

import com.hyuuny.resellshop.orders.domain.Order

interface OrderWriter {

    fun write(newOrder: NewOrder): Order

    fun cancel(order: Order)
}