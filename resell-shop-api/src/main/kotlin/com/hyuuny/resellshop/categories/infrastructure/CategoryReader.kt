package com.hyuuny.resellshop.categories.infrastructure

import com.hyuuny.resellshop.categories.domain.Category

interface CategoryReader {

    fun read(id: Long): Category

    fun readAll(): List<Category>

}