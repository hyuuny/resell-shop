package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.core.common.exception.ProductNotFoundException
import com.hyuuny.resellshop.core.common.response.SimplePage
import com.hyuuny.resellshop.products.dataaccess.ProductRepository
import com.hyuuny.resellshop.products.domain.Product
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ProductReader(
    private val repository: ProductRepository,
) {

    fun read(id: Long): Product =
        repository.findByIdOrNull(id) ?: throw ProductNotFoundException("상품을 찾을 수 없습니다. id: $id")

    fun read(ids: List<Long>): List<Product> = repository.findAllById(ids)

    fun readPage(searchCommand: ProductSearchCommand, pageable: Pageable): SimplePage<Product> {
        return repository.findAllBySearchCommand(searchCommand, pageable)
    }
}