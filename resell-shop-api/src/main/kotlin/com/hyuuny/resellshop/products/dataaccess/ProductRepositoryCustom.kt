package com.hyuuny.resellshop.products.dataaccess

import com.hyuuny.resellshop.core.common.response.SimplePage
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.service.ProductSearchCommand
import org.springframework.data.domain.Pageable

interface ProductRepositoryCustom {
    fun findAllBySearchCommand(searchCommand: ProductSearchCommand, pageable: Pageable): SimplePage<Product>
}
