package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.products.domain.Brand
import com.hyuuny.resellshop.products.infrastructure.NewProduct
import com.hyuuny.resellshop.products.infrastructure.NewProductImage
import com.hyuuny.resellshop.products.infrastructure.NewProductSize
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
    fun toNewProduct(): NewProduct = NewProduct(
        categoryId = categoryId,
        brand = brand,
        nameEn = nameEn,
        nameKo = nameKo,
        releasePrice = releasePrice,
        modelNumber = modelNumber,
        releaseDate = releaseDate,
        option = option,
        images = images.map { NewProductImage(it.imageUrl) },
        sizes = sizes.map { NewProductSize(it.size) }
    )
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
