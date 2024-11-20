package com.hyuuny.resellshop.products.infrastructure

import com.hyuuny.resellshop.core.common.exception.ProductNotFoundException
import com.hyuuny.resellshop.core.common.response.SimplePage
import com.hyuuny.resellshop.products.dataaccess.ProductRepository
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.service.ProductSearchCommand
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ProductReaderImpl(
    private val repository: ProductRepository,
) : ProductReader {

    override fun findById(id: Long): Product =
        repository.findByIdOrNull(id) ?: throw ProductNotFoundException("상품을 찾을 수 없습니다. id: $id")

    override fun findAllBySearchCommand(searchCommand: ProductSearchCommand, pageable: Pageable): SimplePage<Product> {
        return repository.findAllBySearchCommand(searchCommand, pageable)
    }
}