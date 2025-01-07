package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.core.common.response.SimplePage
import com.hyuuny.resellshop.products.domain.Product
import org.springframework.data.domain.Pageable

interface ProductReader {

    fun read(id: Long): Product

    fun read(ids: List<Long>): List<Product>

    fun readPage(searchCommand: ProductSearchCommand, pageable: Pageable): SimplePage<Product>

}