package com.hyuuny.resellshop.products.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "products")
class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    val categoryId: Long,
    @Enumerated(EnumType.STRING) val brand: Brand,
    val nameEn: String,
    val nameKo: String,
    val releasePrice: Price? = null,
    val modelNumber: String,
    val releaseDate: LocalDate? = null,
    val option: String,
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val images: MutableList<ProductImage> = mutableListOf(),
    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val sizes: MutableList<ProductSize> = mutableListOf(),
) {
    companion object {
        fun of(
            categoryId: Long,
            nameEn: String,
            nameKo: String,
            brand: Brand,
            releasePrice: Long?,
            modelNumber: String,
            releaseDate: LocalDate?,
            option: String
        ): Product = Product(
            categoryId = categoryId,
            nameEn = nameEn,
            nameKo = nameKo,
            brand = brand,
            releasePrice = releasePrice?.let { Price(it) },
            modelNumber = modelNumber,
            releaseDate = releaseDate,
            option = option,
        )
    }

    fun addImage(newImage: ProductImage) {
        images.add(newImage)
        newImage.product = this
    }

    fun addImages(newImages: List<ProductImage>) {
        newImages.forEach { addImage(it) }
    }

    fun addSize(newSize: ProductSize) {
        sizes.add(newSize)
        newSize.product = this
    }

    fun addSizes(newSizes: List<ProductSize>) {
        newSizes.forEach { addSize(it) }
    }
}
