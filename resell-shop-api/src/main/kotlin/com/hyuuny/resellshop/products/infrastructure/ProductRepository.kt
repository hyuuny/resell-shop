package com.hyuuny.resellshop.products.infrastructure

import com.hyuuny.resellshop.products.domain.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>, ProductRepositoryCustom {
}
