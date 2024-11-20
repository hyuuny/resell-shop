package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.products.domain.Brand
import com.hyuuny.resellshop.products.infrastructure.InsertProduct
import com.hyuuny.resellshop.products.infrastructure.InsertProductImage
import com.hyuuny.resellshop.products.infrastructure.InsertProductSize
import java.time.LocalDate

data class CreateProductCommand(
    val categoryId: Long,
    val nameEn: String,
    val brand: Brand,
    val nameKo: String,
    val releasePrice: Long?,
    val modelNumber: String,
    val releaseDate: LocalDate?,
    val option: String,
    val images: List<ProductImageCommand>,
    val sizes: List<ProductSizeCommand>,
) {
    fun toInsertProduct(): InsertProduct {
        return InsertProduct(
            categoryId = categoryId,
            brand = brand,
            nameEn = nameEn,
            nameKo = nameKo,
            releasePrice = releasePrice,
            modelNumber = modelNumber,
            releaseDate = releaseDate,
            option = option,
            images = images.map { InsertProductImage(it.imageUrl) },
            sizes = sizes.map { InsertProductSize(it.size) }
        )
    }
}

data class ProductImageCommand(
    val imageUrl: String,
)

data class ProductSizeCommand(
    val size: String,
)

data class ProductSearchCommand(
    val categoryId: Long? = null,
    val brand: Brand? = null,
    val nameKo: String? = null,
)
