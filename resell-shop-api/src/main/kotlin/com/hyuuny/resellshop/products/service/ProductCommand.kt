package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.products.domain.Brand
import java.math.BigDecimal
import java.time.LocalDate

data class CreateProductCommand(
    val categoryId: Long,
    val nameEn: String,
    val brand: Brand,
    val nameKo: String,
    val releasePrice: BigDecimal?,
    val modelNumber: String,
    val releaseDate: LocalDate?,
    val option: String,
    val images: List<ProductImageCommand>,
)

data class ProductImageCommand(
    val imageUrl: String,
)
