package com.hyuuny.resellshop.products.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "product_images")
class ProductImage(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id") var product: Product,
    val imageUrl: String,
) {
    companion object {
        fun of(product: Product, imageUrl: String): ProductImage = ProductImage(product = product, imageUrl = imageUrl)
    }
}
