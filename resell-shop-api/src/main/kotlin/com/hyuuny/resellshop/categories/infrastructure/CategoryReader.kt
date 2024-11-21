package com.hyuuny.resellshop.categories.infrastructure

import com.hyuuny.resellshop.categories.domain.Category

interface CategoryReader {

    fun findById(id: Long): Category

    fun findAll(): List<Category>

}