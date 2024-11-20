package com.hyuuny.resellshop.products.infrastructure

import com.hyuuny.resellshop.core.common.response.SimplePage
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.service.ProductSearchCommand
import org.springframework.data.domain.Pageable

interface ProductReader {

    fun findById(id: Long): Product

    fun findAllBySearchCommand(searchCommand: ProductSearchCommand, pageable: Pageable): SimplePage<Product>

}