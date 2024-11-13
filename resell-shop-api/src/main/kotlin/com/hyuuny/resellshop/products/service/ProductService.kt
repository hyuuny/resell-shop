package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.core.common.exception.ProductNotFoundException
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.domain.ProductImage
import com.hyuuny.resellshop.products.infrastructure.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ProductService(
    private val repository: ProductRepository,
) {

    fun create(command: CreateProductCommand): ProductResponse {
        val product = Product.of(
            categoryId = command.categoryId,
            nameEn = command.nameEn,
            brand = command.brand,
            nameKo = command.nameKo,
            releasePrice = command.releasePrice,
            modelNumber = command.modelNumber,
            releaseDate = command.releaseDate,
            option = command.option,
        )
        val productImages = command.images.map { ProductImage.of(product, it.imageUrl) }
        product.addImages(productImages)

        return ProductResponse(repository.save(product))
    }

    fun getProduct(id: Long): ProductResponse {
        val product = repository.findByIdOrNull(id)
            ?: throw ProductNotFoundException("상품을 찾을 수 없습니다. id: $id")
        return ProductResponse(product)
    }
}
