package com.hyuuny.resellshop.products.infrastructure

import com.hyuuny.resellshop.products.domain.Brand
import java.time.LocalDate

data class InsertProduct(
    val categoryId: Long,
    val nameEn: String,
    val brand: Brand,
    val nameKo: String,
    val releasePrice: Long?,
    val modelNumber: String,
    val releaseDate: LocalDate?,
    val option: String,
    val images: List<InsertProductImage>,
    val sizes: List<InsertProductSize>
)

data class InsertProductImage(
    val imageUrl: String,
)

data class InsertProductSize(
    val size: String,
)