package com.hyuuny.resellshop.categories.infrastructure

import com.hyuuny.resellshop.categories.domain.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long>
