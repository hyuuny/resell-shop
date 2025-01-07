package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.products.domain.Product

interface ProductWriter {

    fun write(newProduct: NewProduct): Product

}