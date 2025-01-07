package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.products.dataaccess.ProductRepository
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.domain.ProductImage
import com.hyuuny.resellshop.products.domain.ProductSize
import org.springframework.stereotype.Component

@Component
class ProductWriter(
    private val repository: ProductRepository,
) {

    fun write(newProduct: NewProduct): Product {
        val product = Product.of(
            categoryId = newProduct.categoryId,
            nameEn = newProduct.nameEn,
            brand = newProduct.brand,
            nameKo = newProduct.nameKo,
            releasePrice = newProduct.releasePrice,
            modelNumber = newProduct.modelNumber,
            releaseDate = newProduct.releaseDate,
            option = newProduct.option,
        )
        val productImages = newProduct.images.map { ProductImage.of(product, it.imageUrl) }
        val productSizes = newProduct.sizes.map { ProductSize.of(product, it.size) }

        product.addImages(productImages)
        product.addSizes(productSizes)

        return repository.save(product)
    }
}