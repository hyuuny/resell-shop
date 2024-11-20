package com.hyuuny.resellshop.products.infrastructure

import com.hyuuny.resellshop.products.domain.Product

interface ProductWriter {

    fun insert(insertProduct: InsertProduct): Product

}