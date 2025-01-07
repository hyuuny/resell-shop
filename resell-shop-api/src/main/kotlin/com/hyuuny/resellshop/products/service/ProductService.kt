package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.core.common.response.SimplePage
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
        val newProduct = writer.write(command.toNewProduct())
        return ProductResponse(newProduct)
    }

    fun getProduct(id: Long): ProductResponse {
        val product = reader.read(id)
        return ProductResponse(product)
    }

    fun search(searchCommand: ProductSearchCommand, pageable: Pageable): SimplePage<ProductSearchResponse> {
        val page = reader.readPage(searchCommand, pageable)
        val content = page.content.map { ProductSearchResponse(it) }
        return SimplePage(content, page)
    }
}
