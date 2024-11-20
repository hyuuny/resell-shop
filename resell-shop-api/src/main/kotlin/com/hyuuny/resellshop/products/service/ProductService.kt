package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.products.infrastructure.ProductReader
import com.hyuuny.resellshop.products.infrastructure.ProductWriter
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ProductService(
    private val writer: ProductWriter,
    private val reader: ProductReader,
) {

    @Transactional
    fun create(command: CreateProductCommand): ProductResponse {
        val newProduct = writer.insert(command.toInsertProduct())
        return ProductResponse(newProduct)
    }

    fun getProduct(id: Long): ProductResponse {
        val product = reader.findById(id)
        return ProductResponse(product)
    }

    fun getAllBySearchCommand(searchCommand: ProductSearchCommand, pageable: Pageable): List<ProductSearchResponse> {
        val page = reader.findAllBySearchCommand(searchCommand, pageable)
        return page.content.map { ProductSearchResponse(it) }
    }
}
