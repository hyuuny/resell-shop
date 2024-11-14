package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.products.domain.Brand
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
)

data class ProductImageCommand(
    val imageUrl: String,
)

data class ProductSearchCommand(
    val categoryId: Long? = null,
    val brand: Brand? = null,
    val nameKo: String? = null,
)
