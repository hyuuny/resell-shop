package com.hyuuny.resellshop.products.dataaccess

import com.hyuuny.resellshop.products.domain.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>, ProductRepositoryCustom {
}
