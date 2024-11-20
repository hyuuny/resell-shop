package com.hyuuny.resellshop.products.infrastructure

import com.hyuuny.resellshop.products.dataaccess.ProductRepository
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.domain.ProductImage
import com.hyuuny.resellshop.products.domain.ProductSize
import org.springframework.stereotype.Component

@Component
class ProductWriterImpl(
    private val repository: ProductRepository,
) : ProductWriter {

    override fun insert(insertProduct: InsertProduct): Product {
        val product = Product.of(
            categoryId = insertProduct.categoryId,
            nameEn = insertProduct.nameEn,
            brand = insertProduct.brand,
            nameKo = insertProduct.nameKo,
            releasePrice = insertProduct.releasePrice,
            modelNumber = insertProduct.modelNumber,
            releaseDate = insertProduct.releaseDate,
            option = insertProduct.option,
        )
        val productImages = insertProduct.images.map { ProductImage.of(product, it.imageUrl) }
        val productSizes = insertProduct.sizes.map { ProductSize.of(product, it.size) }

        product.addImages(productImages)
        product.addSizes(productSizes)

        return repository.save(product)
    }
}