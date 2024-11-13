package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.products.domain.Brand
import com.hyuuny.resellshop.products.domain.Product
import com.hyuuny.resellshop.products.domain.ProductImage
import java.math.BigDecimal
import java.time.LocalDate

data class ProductResponse(
    val id: Long,
    val categoryId: Long,
    val nameEn: String,
    val brand: Brand,
    val nameKo: String,
    val releasePrice: BigDecimal?,
    val modelNumber: String,
    val releaseDate: LocalDate?,
    val option: String,
    val thumbnailUrl: String,
    val images: List<ProductImageResponse>,
) {
    constructor(entity: Product) : this(
        id = entity.id!!,
        categoryId = entity.categoryId,
        nameEn = entity.nameEn,
        brand = entity.brand,
        nameKo = entity.nameKo,
        releasePrice = entity.releasePrice?.amount,
        modelNumber = entity.modelNumber,
        releaseDate = entity.releaseDate,
        option = entity.option,
        thumbnailUrl = entity.images.first().imageUrl,
        images = entity.images.map { ProductImageResponse.of(it) },
    )
}

data class ProductImageResponse(
    val productId: Long,
    val imageUrl: String,
) {
    companion object {
        fun of(entity: ProductImage): ProductImageResponse = ProductImageResponse(
            productId = entity.product.id!!,
            imageUrl = entity.imageUrl
        )
    }
}
