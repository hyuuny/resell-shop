package com.hyuuny.resellshop.products.domain

import jakarta.persistence.*

@Entity
@Table(name = "product_sizes")
class ProductSize(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id") var product: Product,
    val size: String,
) {
    companion object {
        fun of(product: Product, size: String): ProductSize = ProductSize(product = product, size = size)
    }
}
