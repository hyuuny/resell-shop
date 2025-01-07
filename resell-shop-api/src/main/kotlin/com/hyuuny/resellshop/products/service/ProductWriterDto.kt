package com.hyuuny.resellshop.products.service

import com.hyuuny.resellshop.products.domain.Brand
import java.time.LocalDate

data class NewProduct(
    val categoryId: Long,
    val nameEn: String,
    val brand: Brand,
    val nameKo: String,
    val releasePrice: Long?,
    val modelNumber: String,
    val releaseDate: LocalDate?,
    val option: String,
    val images: List<NewProductImage>,
    val sizes: List<NewProductSize>
)

data class NewProductImage(
    val imageUrl: String,
)

data class NewProductSize(
    val size: String,
)