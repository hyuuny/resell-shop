package com.hyuuny.resellshop.orders.infrastructure

import com.hyuuny.resellshop.orders.domain.Order

interface OrderWriter {

    fun insert(insertOrder: InsertOrder): Order

}